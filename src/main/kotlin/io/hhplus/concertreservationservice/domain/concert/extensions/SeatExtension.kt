package io.hhplus.concertreservationservice.domain.concert.extensions

import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.service.response.SeatInfo

fun Seat.toSeatInfo(): SeatInfo {
    return SeatInfo(
        no = this.no,
    )
}
