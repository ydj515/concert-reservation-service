package io.hhplus.concertreservationservice.presentation.controller.concert

import io.hhplus.concertreservationservice.application.job.ReservationExpireJob
import io.hhplus.concertreservationservice.application.job.TokenActivationJob
import io.hhplus.concertreservationservice.application.job.TokenDeactivationJob
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.token.request.ReservationTokenCreateRequest
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcertIntegrationControllerTest(
    @LocalServerPort val port: Int,
    private val tokenJpaRepository: ReservationTokenJpaRepository,
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
        }

        "유효한 토큰, concertId, scheduleId, 예약 가능한 날짜를 가지고 예약 가능한 콘서트를 조회하면 예약 가능한 좌석을 내려준다." {
            val userId = 1L
            val concertId = 1L
            val scheduleId = 1L
            val date = "2024-01-01"

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
                .`when`()
                .get("$baseUrl/$concertId/schedules/$scheduleId/seats?date=$date")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("data.available", equalTo(true))
                .body("data.seats.size()", not(0))
        }

        "예약 가능한 날짜가 없다면 빈 리스트를 반환한다." {
            val userId = 1L
            val concertId = 999L
            val scheduleId = 888L
            val date = "2025-01-01"

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
                .`when`()
                .get("$baseUrl/$concertId/schedules/$scheduleId/seats?date=$date")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("data.available", equalTo(false))
                .body("data.seats.size()", equalTo(0))
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
