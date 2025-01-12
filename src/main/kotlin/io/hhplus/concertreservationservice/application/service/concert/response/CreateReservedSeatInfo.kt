package io.hhplus.concertreservationservice.application.service.concert.response

import io.hhplus.concertreservationservice.application.usecase.concert.response.SeatReserveResult

data class CreateReservedSeatInfo(
    val reservationId: Long,
    val seatNo: Int,
)

fun CreateReservedSeatInfo.toSeatReserveResult(): SeatReserveResult {
    return SeatReserveResult(
        reservationId = reservationId,
        seatNo = seatNo,
    )
}
