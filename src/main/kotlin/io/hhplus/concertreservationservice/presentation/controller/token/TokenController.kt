package io.hhplus.concertreservationservice.presentation.controller.token

import io.hhplus.concertreservationservice.application.facade.token.TokenFacade
import io.hhplus.concertreservationservice.application.facade.token.request.CreateReservationTokenCriteria
import io.hhplus.concertreservationservice.application.facade.token.request.ReservationTokenStatusCriteria
import io.hhplus.concertreservationservice.application.facade.token.response.toResponse
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.token.request.ReservationTokenCreateRequest
import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenCreateResponse
import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenPollingResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reservation-token")
class TokenController(
    private val tokenFacade: TokenFacade,
) {
    @Operation(
        summary = "토큰 발급",
        description = "reservation 토큰을 발급합니다.",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "토큰 발급 성공",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
            ),
        ],
    )
    @PostMapping("")
    fun createReservationToken(
        @RequestBody request: ReservationTokenCreateRequest,
    ): ReservationTokenCreateResponse {
        val criteria = CreateReservationTokenCriteria(request.userId)
        val result = tokenFacade.createToken(criteria)
        return result.toResponse()
    }

    @Operation(
        summary = "토큰 상태 확인(polling 용도)",
        description = "reservation 토큰의 활성화 상태를 확인합니다.",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "token 의 상태를 반환합니다.",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 토큰 요청(header 확인)입니다.",
            ),
        ],
    )
    @GetMapping("/status")
    fun getQueueTokenStatus(
        @RequestHeader(RESERVATION_QUEUE_TOKEN) token: String,
    ): ReservationTokenPollingResponse {
        val criteria = ReservationTokenStatusCriteria(token)
        val result = tokenFacade.getTokenStatus(criteria)
        return result.toResponse()
    }
}
