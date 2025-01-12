package io.hhplus.concertreservationservice.domain.concert.service

import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.exception.SeatNotFoundException
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import io.hhplus.concertreservationservice.domain.concert.service.request.ReserveSeatCommand
import org.springframework.stereotype.Service

@Service
class SeatService(
    private val seatRepository: SeatRepository,
) {
    fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat {
        return seatRepository.getSeatForReservationWithLock(command) ?: throw SeatNotFoundException()
    }
}
