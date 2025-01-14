package io.hhplus.concertreservationservice.domain.concert.repository

import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.service.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.request.SearchAvailSeatCommand
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface ConcertRepository {
    fun getConcert(id: Long): Optional<Concert>

    fun getConcerts(pageable: Pageable): Page<Concert>

    fun saveConcert(concert: Concert): Concert

    fun getSchedule(id: Long): Optional<Schedule>

    fun getAvailableSeats(command: SearchAvailSeatCommand): List<Seat>

    fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat?
}
