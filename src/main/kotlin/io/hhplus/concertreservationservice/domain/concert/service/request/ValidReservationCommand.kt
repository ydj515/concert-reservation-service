package io.hhplus.concertreservationservice.domain.concert.service.request

data class ValidReservationCommand(
    val reservationId: Long,
    val userId: Long,
)
