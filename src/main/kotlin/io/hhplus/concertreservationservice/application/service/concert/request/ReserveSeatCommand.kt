package io.hhplus.concertreservationservice.application.service.concert.request

data class ReserveSeatCommand(
    val scheduleId: Long,
    val seatNo: Int,
)
