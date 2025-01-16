package io.hhplus.concertreservationservice.presentation.controller.payment

import io.hhplus.concertreservationservice.application.job.ReservationExpireJob
import io.hhplus.concertreservationservice.application.job.TokenActivationJob
import io.hhplus.concertreservationservice.application.job.TokenDeactivationJob
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PaymentJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatReservationJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.balance.request.BalanceChargeRequest
import io.hhplus.concertreservationservice.presentation.controller.concert.request.ReservationSeatRequest
import io.hhplus.concertreservationservice.presentation.controller.payment.request.PaymentRequest
import io.hhplus.concertreservationservice.presentation.controller.token.request.ReservationTokenCreateRequest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentIntegrationControllerTest(
    @LocalServerPort val port: Int,
    private val tokenJpaRepository: ReservationTokenJpaRepository,
    private val reservationJpaRepository: SeatReservationJpaRepository,
    private val paymentJpaRepository: PaymentJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : StringSpec({

        val reservationExpireJob = mockk<ReservationExpireJob>()
        val tokenActivationJob = mockk<TokenActivationJob>()
        val tokenDeactivationJob = mockk<TokenDeactivationJob>()

        val baseUrl = "/api/payment"
        val concertUrl = "/api/concert"
        val balanceUrl = "/api/balance"
        val createTokenUrl = "/reservation-token"
        val userId = 1L

        beforeTest {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = port
        }
        afterTest {
            tokenJpaRepository.deleteAllInBatch()
            paymentJpaRepository.deleteAllInBatch()
            reservationJpaRepository.deleteAllInBatch()
            userJpaRepository.save(User(userId, "test1", Money(0)))
        }

        "유효한 토큰을 가지고 결제를 진행하면 결제가 완료된다." {
            val concertId = 1L
            val scheduleId = 1L
            val seatNo = 1
            val balanceChargeAmount = 10000L
            val amount = 10000L
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

            val userBalanceAmount: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .body(BalanceChargeRequest(balanceChargeAmount))
                    .`when`()
                    .post(balanceUrl)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .extract()
                    .path("data.balance")

            val reservationId: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .body(ReservationSeatRequest(seatNo))
                    .`when`()
                    .post("$concertUrl/$concertId/schedules/$scheduleId/reservations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .extract()
                    .path("data.reservationId")

            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .body(PaymentRequest(reservationId, amount))
                .`when`()
                .post(baseUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
        }

        "동시에 10번  유효한 토큰을 가지고 결제를 진행하면 결제가 한번만 완료된다." {
            val concertId = 1L
            val scheduleId = 1L
            val seatNo = 1
            val balanceChargeAmount = 10000L
            val amount = 10000L
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

            val userBalanceAmount: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .body(BalanceChargeRequest(balanceChargeAmount))
                    .`when`()
                    .post(balanceUrl)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .extract()
                    .path("data.balance")

            val reservationId: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .body(ReservationSeatRequest(seatNo))
                    .`when`()
                    .post("$concertUrl/$concertId/schedules/$scheduleId/reservations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .extract()
                    .path("data.reservationId")

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
                            .body(PaymentRequest(reservationId, amount))
                            .`when`()
                            .post(baseUrl)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .body("success", equalTo(true))
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

            successfulApplications.get() shouldBe 1
        }

        "유효하지 않은 예약ID를 요청하면 결제에 실패한다." {
            val reservationId = -999L
            val amount: Long = 100
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
        }

        "유효하지 않은 금액으로 결제 요청하면 결제에 실패한다." {
            val concertId = 1L
            val scheduleId = 1L
            val seatNo = 1
            val amount = -999L
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

            val reservationId: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .body(ReservationSeatRequest(seatNo))
                    .`when`()
                    .post("$concertUrl/$concertId/schedules/$scheduleId/reservations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .extract()
                    .path("data.reservationId")

            assertThrows<IllegalArgumentException> {
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .body(PaymentRequest(reservationId, amount))
                    .`when`()
                    .post(baseUrl)
            }
        }

        "유효한 요청이지만 잔액이 모자랄 경우 결제 요청하면 결제에 실패한다." {

            val concertId = 1L
            val scheduleId = 1L
            val seatNo = 1
            val amount = 10000000L
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

            val reservationId: Long =
                given()
                    .contentType(ContentType.JSON)
                    .header(RESERVATION_QUEUE_TOKEN, token)
                    .body(ReservationSeatRequest(seatNo))
                    .`when`()
                    .post("$concertUrl/$concertId/schedules/$scheduleId/reservations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .extract()
                    .path("data.reservationId")

            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .body(PaymentRequest(reservationId, amount))
                .`when`()
                .post(baseUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
        }

        "잘못된 토큰을 가지고 결제를 진행하면 401 에러를 받는다." {
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
