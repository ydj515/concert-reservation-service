package io.hhplus.concertreservationservice.presentation.controller.payment

import io.hhplus.concertreservationservice.application.facade.payment.PaymentUseCase
import io.hhplus.concertreservationservice.application.facade.payment.request.ProcessPaymentCriteria
import io.hhplus.concertreservationservice.application.facade.payment.response.toResponse
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.payment.request.PaymentRequest
import io.hhplus.concertreservationservice.presentation.controller.payment.response.PaymentResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payment")
class PaymentController(
    private val paymentUseCase: PaymentUseCase,
) {
    @Operation(
        summary = "결제 진행",
        description = "예약건에 대해서 결제를 진행합니다.",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "결제 완료",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "예약 ID 혹은 결제 금액이 맞지 않습니다.",
            ),
        ],
    )
    @PostMapping("")
    fun processPayment(
        @RequestHeader(RESERVATION_QUEUE_TOKEN) token: String,
        @RequestBody paymentRequest: PaymentRequest,
    ): PaymentResponse {
        val criteria = ProcessPaymentCriteria(token, paymentRequest.reservationId, paymentRequest.amount)
        val result = paymentUseCase.processPayment(criteria)
        return result.toResponse()
    }
}
