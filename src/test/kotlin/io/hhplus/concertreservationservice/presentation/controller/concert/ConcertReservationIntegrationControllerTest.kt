package io.hhplus.concertreservationservice.presentation.controller.concert

import io.hhplus.concertreservationservice.application.job.ReservationExpireJob
import io.hhplus.concertreservationservice.application.job.TokenActivationJob
import io.hhplus.concertreservationservice.application.job.TokenDeactivationJob
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatReservationJpaRepository
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.concert.request.ReservationSeatRequest
import io.hhplus.concertreservationservice.presentation.controller.token.request.ReservationTokenCreateRequest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcertReservationIntegrationControllerTest(
    @LocalServerPort val port: Int,
    private val tokenJpaRepository: ReservationTokenJpaRepository,
    private val reservationJpaRepository: SeatReservationJpaRepository,
) : StringSpec({

        val reservationExpireJob = mockk<ReservationExpireJob>()
        val tokenActivationJob = mockk<TokenActivationJob>()
        val tokenDeactivationJob = mockk<TokenDeactivationJob>()

        val baseUrl = "/api/concert"
        val createTokenUrl = "/reservation-token"

        beforeTest {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = port
        }
        afterTest {
            tokenJpaRepository.deleteAllInBatch()
            reservationJpaRepository.deleteAllInBatch()
        }

        "유효한 요청으로 좌석을 에약하면 예약ID와 예약한 좌석 번호를 응답받는다." {
            val userId = 1L
            val concertId = 1L
            val scheduleId = 1L
            val seatNo = 1
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

            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .body(ReservationSeatRequest(seatNo))
                .`when`()
                .post("$baseUrl/$concertId/schedules/$scheduleId/reservations")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("data.reservationId", notNullValue())
                .body("data.seatNo", equalTo(seatNo))
        }

        "동시에 10번 유효한 요청으로 좌석을 에약하면 하나의 예약만 성공한다." {
            val userId = 1L
            val concertId = 1L
            val scheduleId = 1L
            val seatNo = 1
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
                            .body(ReservationSeatRequest(seatNo))
                            .`when`()
                            .post("$baseUrl/$concertId/schedules/$scheduleId/reservations")
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

        "잘못된 토큰을 가지고 잔액 충전 요청을 하면 401 에러를 받는다." {
            val token = "Invalid-token"
            val userId = 1L
            val concertId = 1L
            val scheduleId = 1L
            val seatNo = 1
            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .body(ReservationSeatRequest(seatNo))
                .`when`()
                .post("$baseUrl/$concertId/schedules/$scheduleId/reservations")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("data", equalTo("Invalid or missing $RESERVATION_QUEUE_TOKEN header"))
        }
    })
