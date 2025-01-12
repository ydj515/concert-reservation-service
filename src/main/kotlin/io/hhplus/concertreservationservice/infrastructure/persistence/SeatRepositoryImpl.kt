package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.application.service.concert.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.application.service.concert.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatJpaRepository
import org.springframework.stereotype.Repository

@Repository
class SeatRepositoryImpl(
    private val seatJpaRepository: SeatJpaRepository,
) : SeatRepository {
    override fun getAvailableSeats(command: SearchAvailSeatCommand): List<Seat> {
        return seatJpaRepository.findAvailableSeatsByConcertAndSchedule(command)
    }

    override fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat? {
        return seatJpaRepository.findByNoAndScheduleSeat_Schedule_Id(command.seatNo, command.scheduleId)
    }
}
