package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.domain.concert.repository.SeatReservationRepository
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatReservationJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SeatReservationRepositoryImpl(
    private val seatReservationJpaRepository: SeatReservationJpaRepository,
) : SeatReservationRepository {
    override fun createReservation(seatReservation: SeatReservation): SeatReservation {
        return seatReservationJpaRepository.save(seatReservation)
    }

    override fun findReservedSeatWithLock(
        seatNo: Int,
        scheduleId: Long,
    ): SeatReservation? {
        return seatReservationJpaRepository.findReservedSeatBySeatNoAndScheduleId(seatNo, scheduleId)
    }

    override fun findReservationWithLock(reservationId: Long): SeatReservation? {
        return seatReservationJpaRepository.findReservationWithLock(reservationId)
    }

    override fun saveReservation(reservation: SeatReservation): SeatReservation {
        return seatReservationJpaRepository.save(reservation)
    }

    override fun saveAllReservations(reservations: List<SeatReservation>): List<SeatReservation> {
        return seatReservationJpaRepository.saveAll(reservations)
    }

    override fun getExpiredReservations(currentTime: LocalDateTime): List<SeatReservation> {
        return seatReservationJpaRepository.findByReservationExpiredAtBefore(currentTime)
    }

    override fun deleteReservations(reservations: List<SeatReservation>) {
        return seatReservationJpaRepository.deleteAllInBatch(reservations)
    }

    override fun deleteAllReservations() {
        return seatReservationJpaRepository.deleteAllInBatch()
    }
}
