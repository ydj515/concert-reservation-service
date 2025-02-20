package io.hhplus.concertreservationservice.domain.payment.event

data class ExternalPayEvent(
    val userName: String,
    val reservationId: Long,
    val amount: Long,
)
