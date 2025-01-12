package io.hhplus.concertreservationservice.domain.concert.service.request

data class ReserveSeatCommand(
    val scheduleId: Long,
    val seatNo: Int,
)
