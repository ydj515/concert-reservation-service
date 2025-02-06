package io.hhplus.concertreservationservice.application.job

import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.ActiveQueueRedisRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.redis.WaitingQueueRedisRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("integration-test")
@SpringBootTest
class TokenDeactivationJobTest(
    private val tokenDeactivationJob: TokenDeactivationJob,
    private val reservationTokenJpaRepository: ReservationTokenJpaRepository,
    private val activeQueueRedisRepository: ActiveQueueRedisRepository,
    private val waitingQueueRedisRepository: WaitingQueueRedisRepository,
    private val userJpaRepository: UserJpaRepository,
) : BehaviorSpec({
        afterEach {
            reservationTokenJpaRepository.deleteAllInBatch()
            userJpaRepository.deleteAllInBatch()
            activeQueueRedisRepository.deleteAll()
            waitingQueueRedisRepository.deleteAll()
        }

        given("user 5명에 만료된 토큰이 존재할때") {
            val issueTokenCount = 5
            val currentTime = LocalDateTime.now()

            val users = mutableListOf<User>()
            for (i in 1..issueTokenCount) {
                users.add(
                    User(name = "Test User-$i"),
                )
            }

            val savedUsers = userJpaRepository.saveAll(users)

            val tokens = mutableListOf<ReservationToken>()
            savedUsers.forEachIndexed { index, _ ->
                tokens.add(
                    ReservationToken(
                        userId = savedUsers[index].id,
                        token = "test-token-$index",
                        expiredAt = LocalDateTime.now().minusDays(1),
                    ),
                )
            }

            val result = activeQueueRedisRepository.active(tokens, currentTime)

            `when`("토큰 비활성화 작업이 실행되면") {
                tokenDeactivationJob.deactivateTokens(currentTime)

                then("대기 중인 토큰이 전부 삭제된다.") {
                    val waitingTokenCount = waitingQueueRedisRepository.getTotalCount()

                    waitingTokenCount shouldBe 0
                    result shouldBe 5
                }
            }
        }

        given("user 20 명중 만료된 토큰은 5개, 유효한 토큰은 15개일때") {
            val validTokenCount = 15
            val expiredTokenCount = 5
            val currentTime = LocalDateTime.now()

            val users = mutableListOf<User>()
            for (i in 1..validTokenCount + expiredTokenCount) {
                users.add(
                    User(name = "Test User-$i"),
                )
            }

            val savedUsers = userJpaRepository.saveAll(users)

            val tokens = mutableListOf<ReservationToken>()
            savedUsers.forEachIndexed { index, user ->
                val expirationTime =
                    if (index < validTokenCount) {
                        currentTime.plusDays(1)
                    } else {
                        currentTime.minusDays(1)
                    }

                tokens.add(
                    ReservationToken(
                        userId = user.id,
                        token = "test-token-$index",
                        expiredAt = expirationTime,
                    ),
                )
            }

            val expiredTokens = activeQueueRedisRepository.active(tokens.filter { it.expiredAt < currentTime }, currentTime.minusDays(1))
            val activeTokens = activeQueueRedisRepository.active(tokens.filter { it.expiredAt > currentTime }, currentTime)

            `when`("토큰 비활성화 작업이 실행되면") {
                tokenDeactivationJob.deactivateTokens(currentTime.minusMinutes(30))

                then("5개의 만료된 토큰은 delete된다.") {
                    val activateTokenCount = activeQueueRedisRepository.getTotalCount()

                    activateTokenCount shouldBe validTokenCount
                }
            }
        }
    })
