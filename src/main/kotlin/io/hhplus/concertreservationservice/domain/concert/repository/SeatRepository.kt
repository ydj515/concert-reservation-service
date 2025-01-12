package io.hhplus.concertreservationservice.domain.concert.repository

import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.service.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.request.SearchAvailSeatCommand

interface SeatRepository {
    fun getAvailableSeats(command: SearchAvailSeatCommand): List<Seat>

    fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat?
}
