package io.hhplus.concertreservationservice.application.job

import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.ReservationTokenConstants.MAX_TOKEN_COUNT
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("integration-test")
@SpringBootTest
class TokenActivationJobTest(
    private val tokenActivationJob: TokenActivationJob,
    private val reservationTokenJpaRepository: ReservationTokenJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : BehaviorSpec({

        afterEach {
            reservationTokenJpaRepository.deleteAllInBatch()
            userJpaRepository.deleteAllInBatch()
        }

        given("user 5명에 토큰이 발급 되었을때") {
            val issueTokenCount = 5

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
                        expiredAt = LocalDateTime.now().plusDays(1),
                    ),
                )
            }

            val savedTokens = reservationTokenJpaRepository.saveAll(tokens)

            `when`("토큰 활성화 작업이 실행되면") {
                tokenActivationJob.activateTokens(LocalDateTime.now())

                then("대기 중인 토큰이 활성화 상태로 전부 변경된다") {
                    val allTokens = reservationTokenJpaRepository.findAll()
                    val activeCount = allTokens.count { it.status == TokenStatus.ACTIVE }
                    val waitingCount = allTokens.count { it.status == TokenStatus.WAITING }

                    activeCount shouldBe issueTokenCount
                    waitingCount shouldBe 0
                }
            }
        }

        given("user 110 명에 토큰이 발급 되었을때") {
            val issueTokenCount = 110

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
                        expiredAt = LocalDateTime.now().plusDays(1),
                    ),
                )
            }

            val savedTokens = reservationTokenJpaRepository.saveAll(tokens)

            `when`("토큰 활성화 작업이 실행되면") {
                tokenActivationJob.activateTokens(LocalDateTime.now())

                then("max값인 100개만 활성화 되고 나머지 토큰은 대기 상태로 된다.") {
                    val allTokens = reservationTokenJpaRepository.findAll()
                    val activeCount = allTokens.count { it.status == TokenStatus.ACTIVE }
                    val waitingCount = allTokens.count { it.status == TokenStatus.WAITING }

                    activeCount shouldBe MAX_TOKEN_COUNT
                    waitingCount shouldBe issueTokenCount - MAX_TOKEN_COUNT
                }
            }
        }
    })
