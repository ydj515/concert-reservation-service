package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.common.localDateTimeToDouble
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.domain.token.repository.ReservationTokenRepository
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.ActiveQueueRedisRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.WaitingQueueRedisRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ReservationReservationTokenRepositoryImpl(
    private val reservationTokenJpaRepository: ReservationTokenJpaRepository,
    private val waitingQueueRedisRepository: WaitingQueueRedisRepository,
    private val activeQueueRedisRepository: ActiveQueueRedisRepository,
) : ReservationTokenRepository {
    override fun saveToken(token: ReservationToken): ReservationToken {
        val result = waitingQueueRedisRepository.add(token.token, token.userId, localDateTimeToDouble(token.expiredAt))
        return reservationTokenJpaRepository.save(token)
    }

    override fun findToken(command: TokenStatusCommand): ReservationToken? {
        return activeQueueRedisRepository.findToken(command)
    }

    override fun removeExpiredActiveTokens(currentTime: LocalDateTime): Int {
        return activeQueueRedisRepository.removeExpiredTokens(currentTime)
    }

    override fun deleteTokens(tokens: List<ReservationToken>) {
        reservationTokenJpaRepository.deleteAllInBatch(tokens)
    }

    override fun deleteToken(token: ReservationToken) {
        reservationTokenJpaRepository.delete(token)
    }

    override fun deleteTokenByName(token: String) {
        reservationTokenJpaRepository.deleteByToken(token)
    }

    override fun getActiveTokenCount(status: TokenStatus): Int {
        return activeQueueRedisRepository.getTotalCount()
    }

    override fun getWaitingTokensForActivation(pageable: Pageable): List<ReservationToken> {
        return waitingQueueRedisRepository.getWaitingTokensForActivation(pageable.pageSize.toLong())
    }

    override fun updateToActiveStatus(
        tokens: List<ReservationToken>,
        currentTime: LocalDateTime,
    ): Int {
        return activeQueueRedisRepository.active(tokens, currentTime)
    }
}
