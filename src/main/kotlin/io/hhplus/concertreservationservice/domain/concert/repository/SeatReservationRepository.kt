package io.hhplus.concertreservationservice.domain.concert.repository

import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import java.time.LocalDateTime
import java.util.Optional

interface SeatReservationRepository {
    fun createReservation(seatReservation: SeatReservation): SeatReservation

    fun findReservedSeatWithLock(
        seatNo: Int,
        scheduleId: Long,
    ): Optional<SeatReservation>

    fun findReservationWithLock(reservationId: Long): Optional<SeatReservation>

    fun saveReservation(reservation: SeatReservation): SeatReservation

    fun saveAllReservations(reservations: List<SeatReservation>): List<SeatReservation>

    fun getExpiredReservations(currentTime: LocalDateTime): List<SeatReservation>

    fun deleteReservations(reservations: List<SeatReservation>)

    fun deleteAllReservations()
}
