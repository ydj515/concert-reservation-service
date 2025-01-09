package io.hhplus.concertreservationservice.domain.reservation.extension

import io.hhplus.concertreservationservice.application.service.concert.response.CreateReservedSeatInfo
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation

fun SeatReservation.toCreateReservedSeatInfo(seatNo: Int): CreateReservedSeatInfo {
    return CreateReservedSeatInfo(
        reservationId = this.id,
        seatNo = seatNo,
    )
}
