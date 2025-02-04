package io.hhplus.concertreservationservice.application.facade.payment.response

import io.hhplus.concertreservationservice.domain.payment.PaymentStatus
import io.hhplus.concertreservationservice.presentation.controller.payment.response.PaymentResponse

class ProcessPaymentResult(
    val paymentId: Long,
    val status: PaymentStatus,
)

fun ProcessPaymentResult.toResponse(): PaymentResponse {
    return PaymentResponse(
        status = this.status,
    )
}
