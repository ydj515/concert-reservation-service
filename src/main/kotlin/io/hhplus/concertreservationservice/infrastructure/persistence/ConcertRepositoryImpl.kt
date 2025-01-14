package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.repository.ConcertRepository
import io.hhplus.concertreservationservice.domain.concert.service.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ConcertJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ConcertRepositoryImpl(
    private val concertJpaRepository: ConcertJpaRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val seatJpaRepository: SeatJpaRepository,
) : ConcertRepository {
    override fun getConcert(id: Long): Optional<Concert> {
        return concertJpaRepository.findById(id)
    }

    override fun getConcerts(pageable: Pageable): Page<Concert> {
        return concertJpaRepository.findAll(pageable)
    }

    override fun saveConcert(concert: Concert): Concert {
        return concertJpaRepository.save(concert)
    }

    override fun getSchedule(id: Long): Optional<Schedule> {
        return scheduleJpaRepository.findById(id)
    }

    override fun getAvailableSeats(command: SearchAvailSeatCommand): List<Seat> {
        return seatJpaRepository.findAvailableSeatsByConcertAndSchedule(command)
    }

    override fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat? {
        return seatJpaRepository.findByNoAndScheduleSeat_Schedule_Id(command.seatNo, command.scheduleId)
    }
}
