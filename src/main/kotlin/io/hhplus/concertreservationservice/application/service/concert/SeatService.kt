package io.hhplus.concertreservationservice.application.service.concert

import io.hhplus.concertreservationservice.application.service.concert.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.exception.SeatNotFoundException
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import org.springframework.stereotype.Service

@Service
class SeatService(
    private val seatRepository: SeatRepository,
) {
    fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat {
        return seatRepository.getSeatForReservationWithLock(command).orElseThrow {
            throw SeatNotFoundException()
        }
    }
}
