package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.concert.service.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.domain.concert.Seat
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SeatJpaRepository : JpaRepository<Seat, Long> {
    @Query(
        """
        SELECT s
        FROM Seat s
        JOIN s.scheduleSeat ss
        JOIN ss.schedule sc
        WHERE sc.concert.id = :#{#command.concertId}
          AND sc.id = :#{#command.scheduleId}
          AND sc.performanceDate = :#{#command.date}
          AND NOT EXISTS (
              SELECT 1 
              FROM SeatReservation r
              WHERE r.seat.id = s.id
          )
        """
    )
    fun findAvailableSeatsByConcertAndSchedule(
        @Param("command") command: SearchAvailSeatCommand
    ): List<Seat>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT s 
        FROM Seat s
        JOIN s.scheduleSeat ss
        WHERE s.no = :seatNo
          AND ss.schedule.id = :scheduleId
        """
    )
    fun findByNoAndScheduleSeat_Schedule_Id(seatNo: Int, scheduleId: Long): Seat?
}
