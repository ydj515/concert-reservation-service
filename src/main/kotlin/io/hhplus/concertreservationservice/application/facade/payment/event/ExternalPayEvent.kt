package io.hhplus.concertreservationservice.application.facade.payment.event

data class ExternalPayEvent(
    val userName: String,
    val reservationId: Long,
    val amount: Long,
)
