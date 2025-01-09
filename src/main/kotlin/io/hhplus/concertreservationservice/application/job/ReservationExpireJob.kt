package io.hhplus.concertreservationservice.application.job

import io.hhplus.concertreservationservice.domain.concert.repository.SeatReservationRepository
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class ReservationExpireJob(
    private val reservationRepository: SeatReservationRepository,
) : Job {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        logger.info("Executing ReservationExpire at ${LocalDateTime.now()} by instance: ${context.scheduler.schedulerInstanceId}")
        expireReservation(LocalDateTime.now())
    }

    fun expireReservation(currentTime: LocalDateTime) {
        val reservations = reservationRepository.getExpiredReservations(currentTime)
        if (reservations.isNotEmpty()) {
            reservationRepository.deleteReservations(reservations)
            logger.info("Reservation expired delete completed successfully")
        }
    }
}
