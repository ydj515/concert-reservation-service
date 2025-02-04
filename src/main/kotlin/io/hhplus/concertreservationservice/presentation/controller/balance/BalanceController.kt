package io.hhplus.concertreservationservice.presentation.controller.balance

import io.hhplus.concertreservationservice.application.facade.balance.BalanceUseCase
import io.hhplus.concertreservationservice.application.facade.balance.request.ChargeBalanceCriteria
import io.hhplus.concertreservationservice.application.facade.balance.request.FetchBalanceCriteria
import io.hhplus.concertreservationservice.application.facade.balance.response.toResponse
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.balance.request.BalanceChargeRequest
import io.hhplus.concertreservationservice.presentation.controller.balance.response.BalanceChargeResponse
import io.hhplus.concertreservationservice.presentation.controller.balance.response.BalanceFetchResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/balance")
class BalanceController(
    private val balanceUsecase: BalanceUseCase,
) {
    @Operation(
        summary = "잔액 충전",
        description = "유저의 잔액을 충전합니다.",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "충전 완료",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 토큰 요청(header 확인)이거나 충전하는 금액이 음수입니다.",
            ),
        ],
    )
    @PostMapping("")
    fun chargeBalance(
        @RequestHeader(RESERVATION_QUEUE_TOKEN) token: String,
        @RequestBody balanceRequest: BalanceChargeRequest,
    ): BalanceChargeResponse {
        val criteria = ChargeBalanceCriteria(Money(balanceRequest.amount), token)
        val result = balanceUsecase.chargeBalance(criteria)
        return result.toResponse()
    }

    @Operation(
        summary = "잔액 조회",
        description = "유저의 잔액을 조회합니다.",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "잔액 조회 완료",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 토큰 요청(header 확인)입니다.",
            ),
        ],
    )
    @GetMapping("")
    fun getBalance(
        @RequestHeader(RESERVATION_QUEUE_TOKEN) token: String,
    ): BalanceFetchResponse {
        val criteria = FetchBalanceCriteria(token)
        val result = balanceUsecase.getBalance(criteria)
        return result.toResponse()
    }
}
