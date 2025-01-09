package io.hhplus.concertreservationservice.application.service.concert

import io.hhplus.concertreservationservice.application.service.concert.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.application.service.concert.response.SeatInfo
import io.hhplus.concertreservationservice.domain.concert.extensions.toSeatInfo
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val seatRepository: SeatRepository,
) {
    fun getAvailableSeats(command: SearchAvailSeatCommand): List<SeatInfo> {
        val seats = seatRepository.getAvailableSeats(command)
        return seats.map { it.toSeatInfo() }
    }
}
