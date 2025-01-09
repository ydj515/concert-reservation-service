package io.hhplus.concertreservationservice.application.facade.concert.request

data class SeatReserveCriteria(
    val concertId: Long,
    val scheduleId: Long,
    val seatNo: Int,
    val token: String,
)
