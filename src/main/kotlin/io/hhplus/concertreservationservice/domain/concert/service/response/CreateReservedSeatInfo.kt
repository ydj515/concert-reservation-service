package io.hhplus.concertreservationservice.domain.concert.service.response

import io.hhplus.concertreservationservice.application.facade.concert.response.SeatReserveResult

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
