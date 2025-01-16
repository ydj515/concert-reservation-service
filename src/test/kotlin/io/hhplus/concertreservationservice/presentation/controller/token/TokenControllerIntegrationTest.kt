package io.hhplus.concertreservationservice.presentation.controller.token

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
import org.hamcrest.CoreMatchers.notNullValue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenControllerIntegrationTest(
    @LocalServerPort val port: Int,
    private val tokenJpaRepository: ReservationTokenJpaRepository,
) : StringSpec({

        val reservationExpireJob = mockk<ReservationExpireJob>()
        val tokenActivationJob = mockk<TokenActivationJob>()
        val tokenDeactivationJob = mockk<TokenDeactivationJob>()

        val baseUrl = "/reservation-token"
        beforeTest {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = port
        }
        afterTest {
            tokenJpaRepository.deleteAllInBatch()
        }

        "유효한 사용자 id로 토큰발급을 요청하면 토큰을 발급한다." {
            given()
                .contentType(ContentType.JSON)
                .body(ReservationTokenCreateRequest(userId = 1L))
                .`when`()
                .post(baseUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("data.token", notNullValue())
        }

        "유효하지 않은 사용자 id로 토큰발급을 요청하면 토큰을 발급한다." {
            given()
                .contentType(ContentType.JSON)
                .body(ReservationTokenCreateRequest(userId = -99L))
                .`when`()
                .post(baseUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
        }

        "유효한 사용자 id로 발급된 토큰을 가지고 polling 요청으로 status를 확인하면 정상응답이 내려온다." {
            val userId = 1L
            val token: String =
                given()
                    .contentType(ContentType.JSON)
                    .body(ReservationTokenCreateRequest(userId))
                    .`when`()
                    .post(baseUrl)
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
                .get("$baseUrl/status")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("data.userId", equalTo(userId.toInt()))
        }

        "잘못된 토큰을 가지고 polling 요청을 하면 401 에러를 받는다." {
            val token = "Invalid-token"
            given()
                .contentType(ContentType.JSON)
                .header(RESERVATION_QUEUE_TOKEN, token)
                .`when`()
                .get("$baseUrl/status")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("data", equalTo("Invalid or missing $RESERVATION_QUEUE_TOKEN header"))
        }
    })
