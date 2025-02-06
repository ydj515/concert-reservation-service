package io.hhplus.concertreservationservice.presentation.controller.balance

import io.hhplus.concertreservationservice.application.job.ReservationExpireJob
import io.hhplus.concertreservationservice.application.job.TokenActivationJob
import io.hhplus.concertreservationservice.application.job.TokenDeactivationJob
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.ActiveQueueRedisRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.WaitingQueueRedisRepository
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.balance.request.BalanceChargeRequest
import io.hhplus.concertreservationservice.presentation.controller.token.request.ReservationTokenCreateRequest
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BalanceIntegrationControllerTest(
    @LocalServerPort val port: Int,
    private val tokenJpaRepository: ReservationTokenJpaRepository,
    private val activeQueueRedisRepository: ActiveQueueRedisRepository,
    private val waitingQueueRedisRepository: WaitingQueueRedisRepository,
) : StringSpec({

        val reservationExpireJob = mockk<ReservationExpireJob>()
        val tokenActivationJob = mockk<TokenActivationJob>()
        val tokenDeactivationJob = mockk<TokenDeactivationJob>()

        val baseUrl = "/api/balance"
        val createTokenUrl = "/reservation-token"

        beforeTest {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = port
        }
        afterTest {
            tokenJpaRepository.deleteAllInBatch()
            activeQueueRedisRepository.deleteAll()
            waitingQueueRedisRepository.deleteAll()
        }

        "유효한 토큰을 가지고 잔액 조회 요청을 하면 user의 잔액을 반환한다." {
            val userId = 1L
            val token: String =
                given()
                    .contentType(ContentType.JSON)
                    .body(ReservationTokenCreateRequest(userId))
                    .`when`()
                    .post(createTokenUrl)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.token", notNullValue())
                    .extract()
                    .path("data.token")

            val reservationToken =
                ReservationToken(
                    userId = userId,
                    token = token,
                    expiredAt = LocalDateTime.now().plusHours(1),
                    status = TokenStatus.WAITING,
                )
            activeQueueRedisRepository.active(listOf(reservationToken), LocalDateTime.now())

            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .`when`()
                .get(baseUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("data.balance", notNullValue())
        }

        "잘못된 토큰을 가지고 잔액 조회 요청을 하면 401 에러를 받는다." {
            val token = "Invalid-token"
            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .`when`()
                .get(baseUrl)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("data", equalTo("Invalid or missing $RESERVATION_QUEUE_TOKEN header"))
        }

        "유효한 토큰을 가지고 잔액 충전 요청을 하면 user의 충전된 잔액을 반환한다." {
            val userId = 1L
            val amount = 1000L
            val token: String =
                given()
                    .contentType(ContentType.JSON)
                    .body(ReservationTokenCreateRequest(userId))
                    .`when`()
                    .post(createTokenUrl)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.token", notNullValue())
                    .extract()
                    .path("data.token")

            val reservationToken =
                ReservationToken(
                    userId = userId,
                    token = token,
                    expiredAt = LocalDateTime.now().plusHours(1),
                    status = TokenStatus.WAITING,
                )
            activeQueueRedisRepository.active(listOf(reservationToken), LocalDateTime.now())

            val initAmount: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .`when`()
                    .get(baseUrl)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .path("data.balance")

            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .body(BalanceChargeRequest(amount))
                .`when`()
                .post(baseUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("data.balance", notNullValue())
                .body("data.balance", equalTo((initAmount + amount).toInt()))
        }

        "동시에 10번 유효한 토큰을 가지고 잔액 충전 요청을 수행하면 user의 충전된 잔액을 반환한다." {
            val userId = 1L
            val amount = 1000L
            val token: String =
                given()
                    .contentType(ContentType.JSON)
                    .body(ReservationTokenCreateRequest(userId))
                    .`when`()
                    .post(createTokenUrl)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.token", notNullValue())
                    .extract()
                    .path("data.token")

            val reservationToken =
                ReservationToken(
                    userId = userId,
                    token = token,
                    expiredAt = LocalDateTime.now().plusHours(1),
                    status = TokenStatus.WAITING,
                )
            activeQueueRedisRepository.active(listOf(reservationToken), LocalDateTime.now())

            val initAmount: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .`when`()
                    .get(baseUrl)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .path("data.balance")

            val threadCount = 10
            val latch = CountDownLatch(threadCount)
            val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
            val successfulApplications = AtomicInteger(0)
            val failApplications = AtomicInteger(0)

            for (i in 1..threadCount) {
                executor.submit {
                    try {
                        given()
                            .contentType(ContentType.JSON)
                            .header(RESERVATION_QUEUE_TOKEN, token)
                            .body(BalanceChargeRequest(amount))
                            .`when`()
                            .post(baseUrl)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .body("success", equalTo(true))
                            .body("data.balance", notNullValue())
                        successfulApplications.incrementAndGet()
                    } catch (e: Exception) {
                        failApplications.incrementAndGet()
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()
            executor.shutdown()

            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .`when`()
                .get(baseUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.balance", equalTo((initAmount + amount * threadCount).toInt()))
        }

        "잘못된 토큰을 가지고 잔액 충전 요청을 하면 401 에러를 받는다." {
            val token = "Invalid-token"
            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .`when`()
                .post(baseUrl)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("data", equalTo("Invalid or missing $RESERVATION_QUEUE_TOKEN header"))
        }
    })
