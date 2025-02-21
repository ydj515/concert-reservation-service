package io.hhplus.concertreservationservice.domain.payment.event

import io.hhplus.concertreservationservice.domain.payment.PaymentStatus

data class PaymentCompletedEvent(
    val paymentId: Long,
    val status: PaymentStatus,
    val username: String,
    val reservationId: Long,
    val amount: Long,
)
