package io.hhplus.concertreservationservice.domain.concert.service.request

import io.hhplus.concertreservationservice.domain.user.User

data class CreateReserveSeatCommand(
    val concertId: Long,
    val scheduleId: Long,
    val seatNo: Int,
    val user: User,
)
