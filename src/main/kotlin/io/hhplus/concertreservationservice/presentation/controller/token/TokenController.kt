package io.hhplus.concertreservationservice.presentation.controller.token

import io.hhplus.concertreservationservice.application.usecase.token.TokenUseCase
import io.hhplus.concertreservationservice.application.usecase.token.request.CreateReservationTokenCriteria
import io.hhplus.concertreservationservice.application.usecase.token.request.ReservationTokenStatusCriteria
import io.hhplus.concertreservationservice.application.usecase.token.response.toReservationTokenCreateResponse
import io.hhplus.concertreservationservice.application.usecase.token.response.toReservationTokenStatusResponse
import io.hhplus.concertreservationservice.common.response.ApiResponse
import io.hhplus.concertreservationservice.common.response.SuccessResponse
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.token.request.ReservationTokenCreateRequest
import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenCreateResponse
import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenStatusResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reservation-token")
class TokenController(
    private val tokenUseCase: TokenUseCase,
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
    ): ResponseEntity<SuccessResponse<ReservationTokenCreateResponse>> {
        val criteria = CreateReservationTokenCriteria(request.userId)
        val result = tokenUseCase.createToken(criteria)
        return ApiResponse.success(result.toReservationTokenCreateResponse())
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
    ): ResponseEntity<SuccessResponse<ReservationTokenStatusResponse>> {
        val criteria = ReservationTokenStatusCriteria(token)
        val result = tokenUseCase.getTokenStatus(criteria)
        return ApiResponse.success(result.toReservationTokenStatusResponse())
    }
}
