package io.hhplus.concertreservationservice.domain.concert.extensions

import io.hhplus.concertreservationservice.application.service.concert.response.SeatInfo
import io.hhplus.concertreservationservice.domain.concert.Seat

fun Seat.toSeatInfo(): SeatInfo {
    return SeatInfo(
        no = this.no,
    )
}
