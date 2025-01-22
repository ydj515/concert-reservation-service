package io.hhplus.concertreservationservice.application.usecase.concert.response

import io.hhplus.concertreservationservice.presentation.controller.concert.response.ReservationSeatResponse

data class SeatReserveResult(
    val reservationId: Long,
    val seatNo: Int,
)

fun SeatReserveResult.toResponse(): ReservationSeatResponse {
    return ReservationSeatResponse(
        reservationId = this.reservationId,
        seatNo = this.seatNo,
    )
}
