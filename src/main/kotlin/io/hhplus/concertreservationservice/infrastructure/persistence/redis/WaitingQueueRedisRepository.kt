package io.hhplus.concertreservationservice.infrastructure.persistence.redis

import io.hhplus.concertreservationservice.common.doubleToLocalDateTime
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.WaitingQueueRedisRepository.Key.CONCERT_WAITING_QUEUE
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class WaitingQueueRedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private object Key {
        const val CONCERT_WAITING_QUEUE = "concert:waiting_queue"
    }

    // 대기열 토큰 갯수
    fun getTotalCount(): Int {
        return redisTemplate.opsForZSet().zCard(CONCERT_WAITING_QUEUE)?.toInt() ?: 0
    }

    // 대기열 추가
    fun add(
        token: String,
        userId: Long,
        expiredAt: Double,
    ): Boolean {
        return redisTemplate.opsForZSet().add(CONCERT_WAITING_QUEUE, token, expiredAt) ?: false
    }

    // 활성화할 토큰 get
    fun getWaitingTokensForActivation(count: Long): List<ReservationToken> {
        val result = redisTemplate.opsForZSet().popMin(CONCERT_WAITING_QUEUE, count)

        return result?.map {
            val tokenParts = it.value.toString().split(":")
            ReservationToken(
                expiredAt = doubleToLocalDateTime(it.score!!),
                status = TokenStatus.WAITING,
                token = tokenParts[0],
                userId = 0L,
            )
        } ?: emptyList()
    }

    // 토큰 상태(rank, isExist..) 체크
    fun findRank(command: TokenStatusCommand): Long? {
        return redisTemplate.opsForZSet().rank(CONCERT_WAITING_QUEUE, command.token)
    }

    // 전체 삭제
    fun deleteAll() {
        redisTemplate.delete(CONCERT_WAITING_QUEUE)
    }
}
