package io.hhplus.concertreservationservice.application.job

import io.hhplus.concertreservationservice.domain.token.repository.ReservationTokenRepository
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class TokenDeactivationJob(
    private val tokenRepository: ReservationTokenRepository,
) : Job {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        logger.info("Executing TokenDeactivationJob at ${LocalDateTime.now()} by instance: ${context.scheduler.schedulerInstanceId}")
        deactivateTokens(LocalDateTime.now())
    }

    fun deactivateTokens(currentTime: LocalDateTime) {
        val reservations = tokenRepository.getExpiredToken(currentTime)
        if (reservations.isNotEmpty()) {
            tokenRepository.deleteTokens(reservations)
            logger.info("Token deactivation completed successfully")
        }
    }
}
