package io.hhplus.concertreservationservice.application.facade.balance

import io.hhplus.concertreservationservice.application.facade.balance.request.ChargeBalanceCriteria
import io.hhplus.concertreservationservice.application.facade.balance.request.FetchBalanceCriteria
import io.hhplus.concertreservationservice.application.service.balance.BalanceService
import io.hhplus.concertreservationservice.application.service.balance.request.FetchBalanceCommand
import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.exception.TokenNotFoundException
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.TokenProvider
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ActiveProfiles("integration-test")
@SpringBootTest
@Transactional
class BalanceFacadeTest
    @Autowired
    constructor(
        private val balanceFacade: BalanceFacade,
        private val balanceService: BalanceService,
        private val tokenProvider: TokenProvider,
        private val userJpaRepository: UserJpaRepository,
        private val tokenJpaRepository: ReservationTokenJpaRepository,
    ) : BehaviorSpec({

            afterEach {
                tokenJpaRepository.deleteAll()
                userJpaRepository.deleteAll()
            }

            given("유효한 토큰과 금액이 주어졌을 때") {
                val user =
                    userJpaRepository.save(
                        User(
                            name = "testUser",
                        ),
                    )
                val token = tokenProvider.generateToken(user.id)
                tokenJpaRepository.save(
                    ReservationToken(
                        expiredAt = LocalDateTime.now(),
                        token = token,
                        userId = user.id,
                    ),
                )
                val chargeAmount = 1000L
                val initialBalance = balanceService.getBalance(FetchBalanceCommand(user.id)).amount.amount

                `when`("잔액을 충전하고 조회하면") {
                    balanceFacade.chargeBalance(ChargeBalanceCriteria(Money(chargeAmount), token))
                    val fetchResult = balanceFacade.getBalance(FetchBalanceCriteria(token))

                    then("잔액에 충전 금액이 반영된다") {
                        fetchResult.balance.amount shouldBe initialBalance + chargeAmount
                    }
                }
            }

            given("유효하지 않은 토큰이 주어졌을 때") {
                val invalidToken = "invalid_token"

                `when`("잔액 조회를 시도하면") {
                    then("예외가 발생한다") {
                        shouldThrow<TokenNotFoundException> {
                            balanceFacade.getBalance(FetchBalanceCriteria(invalidToken))
                        }
                    }
                }
            }

            given("여러 스레드가 동시에 충전 요청을 보낼 때") {
                val user =
                    userJpaRepository.save(
                        User(
                            name = "testUser",
                        ),
                    )
                val token = tokenProvider.generateToken(user.id)
                tokenJpaRepository.save(
                    ReservationToken(
                        expiredAt = LocalDateTime.now().plusDays(1),
                        token = token,
                        userId = user.id,
                    ),
                )
                val initialBalance = balanceService.getBalance(FetchBalanceCommand(user.id)).amount.amount
                val chargeAmount = 100L
                val threadCount = 10

                `when`("동시에 잔액 충전 요청이 처리되면") {
                    val latch = CountDownLatch(threadCount)
                    val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)

                    for (i in 1..threadCount) {
                        executor.submit {
                            try {
                                balanceFacade.chargeBalance(ChargeBalanceCriteria(Money(chargeAmount), token))
                            } finally {
                                latch.countDown()
                            }
                        }
                    }

                    latch.await()
                    executor.shutdown()

                    val finalBalance = balanceFacade.getBalance(FetchBalanceCriteria(token)).balance

                    then("충전 요청의 총합이 최종 잔액에 반영된다") {
                        finalBalance.amount shouldBe initialBalance + (chargeAmount * threadCount)
                    }
                }
            }
        })
