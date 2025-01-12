package io.hhplus.concertreservationservice.domain.concert.repository

import io.hhplus.concertreservationservice.application.service.concert.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.application.service.concert.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.domain.concert.Seat

interface SeatRepository {
    fun getAvailableSeats(command: SearchAvailSeatCommand): List<Seat>

    fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat?
}
