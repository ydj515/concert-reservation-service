package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface SeatReservationJpaRepository : JpaRepository<SeatReservation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT sr
        FROM SeatReservation sr
        JOIN sr.seat s
        JOIN s.scheduleSeat ss
        JOIN ss.schedule sc
        WHERE s.no = :seatNo
          AND sc.id = :scheduleId
          AND sr.status = 'RESERVED'
        """
    )
    fun findReservedSeatBySeatNoAndScheduleId(
        @Param("seatNo") seatNo: Int,
        @Param("scheduleId") scheduleId: Long
    ): SeatReservation?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM SeatReservation r WHERE r.id = :#{#reservationId}")
    fun findReservationWithLock(@Param("reservationId") reservationId: Long): SeatReservation?

    @Query(
        """
        SELECT sr 
        FROM SeatReservation sr 
        WHERE sr.reservationExpiredAt < CURRENT_TIMESTAMP
        """
    )
    fun findByReservationExpiredAtBefore(currentTime: LocalDateTime): List<SeatReservation>
}
