package io.hhplus.concertreservationservice.application.job

import io.hhplus.concertreservationservice.domain.token.ReservationTokenConstants.MAX_TOKEN_COUNT
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.domain.token.repository.ReservationTokenRepository
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class TokenActivationJob(
    private val tokenRepository: ReservationTokenRepository,
) : Job {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        logger.info("Executing TokenActivationJob at ${LocalDateTime.now()} by instance: ${context.scheduler.schedulerInstanceId}")
        activateTokens(LocalDateTime.now())
    }

    fun activateTokens(currentTime: LocalDateTime) {
        val activeCount = tokenRepository.getActiveTokenCount(TokenStatus.ACTIVE)
        val tokensToActivate = MAX_TOKEN_COUNT - activeCount

        if (tokensToActivate > 0) {
            val waitingTokens =
                tokenRepository.getWaitingTokensForActivation(
                    PageRequest.of(0, tokensToActivate),
                )

            tokenRepository.updateToActiveStatus(waitingTokens, currentTime)
        }
    }
}
