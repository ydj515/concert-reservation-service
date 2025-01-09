package io.hhplus.concertreservationservice.application.service.payment.response

import io.hhplus.concertreservationservice.application.facade.payment.response.ProcessPaymentResult
import io.hhplus.concertreservationservice.domain.payment.PaymentStatus

class ProcessPaymentInfo(
    val paymentId: Long,
    val status: PaymentStatus,
)

fun ProcessPaymentInfo.toProcessPaymentResult(): ProcessPaymentResult {
    return ProcessPaymentResult(
        paymentId = this.paymentId,
        status = this.status,
    )
}
