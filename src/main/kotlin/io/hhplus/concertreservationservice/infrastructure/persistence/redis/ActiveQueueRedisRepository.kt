package io.hhplus.concertreservationservice.infrastructure.persistence.redis

import io.hhplus.concertreservationservice.common.localDateTimeToLong
import io.hhplus.concertreservationservice.common.longToLocalDateTime
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.ActiveQueueRedisRepository.Key.CONCERT_ACTIVATE_QUEUE
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.ActiveQueueRedisRepository.Key.VALID_DURATION_HOUR
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class ActiveQueueRedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private object Key {
        const val CONCERT_ACTIVATE_QUEUE = "concert:active_queue"
        const val VALID_DURATION_HOUR = 1L
    }

    // 활성화 토큰 갯수
    fun getTotalCount(): Int {
        return redisTemplate.opsForSet().size(CONCERT_ACTIVATE_QUEUE)?.toInt() ?: 0
    }

    // 활성화
    fun active(
        tokens: List<ReservationToken>,
        currentTime: LocalDateTime,
    ): Int {
        var sum = 0
        tokens.map {
            val expiredAt = currentTime.plusHours(VALID_DURATION_HOUR)
            val parsedExpiredAt = localDateTimeToLong(expiredAt)
            redisTemplate.opsForSet().add(CONCERT_ACTIVATE_QUEUE, "${it.token}:${it.userId}:$parsedExpiredAt")
            sum++
        }
        return sum
    }

    // 전체 삭제
    fun deleteAll() {
        redisTemplate.delete(CONCERT_ACTIVATE_QUEUE)
    }
}
