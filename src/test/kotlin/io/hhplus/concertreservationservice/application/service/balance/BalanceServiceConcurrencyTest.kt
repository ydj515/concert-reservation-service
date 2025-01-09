package io.hhplus.concertreservationservice.application.service.balance

import io.hhplus.concertreservationservice.application.service.balance.request.ChargeBalanceCommand
import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.domain.user.repository.UserRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest
@ActiveProfiles("integration-test")
@Transactional
class BalanceServiceConcurrencyTest
    @Autowired
    constructor(
        private val userRepository: UserRepository,
        private val userJpaRepository: UserJpaRepository,
        private val balanceService: BalanceService,
    ) : BehaviorSpec({

            afterTest {
                userJpaRepository.deleteAll()
            }

            given("동시성 잔액 충전 테스트") {
                val initialBalance = Money(100)
                val user =
                    userRepository.save(
                        User(
                            name = "길길",
                            balance = initialBalance,
                        ),
                    )
                val chargeAmount = Money(50)
                val command = ChargeBalanceCommand(user.id, chargeAmount)

                `when`("여러 스레드에서 동시에 잔액 충전 요청을 하면") {
                    val executorService: ExecutorService = Executors.newFixedThreadPool(10)
                    val tasks =
                        List(10) {
                            Runnable {
                                balanceService.chargeBalance(command)
                            }
                        }

                    tasks.forEach { executorService.submit(it) }

                    executorService.shutdown()
                    while (!executorService.isTerminated) {
                        Thread.sleep(100)
                    }

                    then("충전된 잔액이 예상대로 계산된다") {
                        val finalUser = userJpaRepository.findById(user.id).get()
                        finalUser.balance.amount shouldBe initialBalance.amount + chargeAmount.amount * 10
                    }
                }
            }
        })
