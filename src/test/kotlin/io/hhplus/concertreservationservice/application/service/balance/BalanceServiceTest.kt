package io.hhplus.concertreservationservice.application.service.balance

import io.hhplus.concertreservationservice.application.service.balance.request.ChargeBalanceCommand
import io.hhplus.concertreservationservice.application.service.balance.request.FetchBalanceCommand
import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.domain.user.exception.UserNotFoundException
import io.hhplus.concertreservationservice.domain.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class BalanceServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val balanceService = BalanceService(userRepository)

    val userId = 1L
    val initialBalance = Money(100)
    val chargeAmount = Money(50)
    val newBalance = initialBalance.add(chargeAmount)

    val user =
        User(
            id = userId,
            name = "John Doe",
            balance = initialBalance,
        )

    given("잔액 충전 요청이 있을 때") {

        every { userRepository.getUserByIdWithLock(userId) } returns Optional.of(user)
        every { userRepository.save(any()) } returns
            User(
                id = userId,
                name = "John Doe",
                balance = newBalance,
            )

        val command = ChargeBalanceCommand(userId, chargeAmount)

        `when`("잔액 충전 작업을 실행하면") {

            val result = balanceService.chargeBalance(command)

            then("충전된 잔액이 반환된다") {
                result.userId shouldBe userId
                result.amount shouldBe newBalance
            }

            then("사용자의 잔액이 업데이트되고 저장된다") {
                verify { userRepository.save(any()) }
            }
        }

        given("사용자가 존재하지 않으면") {

            every { userRepository.getUserByIdWithLock(userId) } returns Optional.empty()

            `when`("잔액 충전 작업을 실행하면") {

                val exception =
                    shouldThrow<UserNotFoundException> {
                        balanceService.chargeBalance(command)
                    }

                then("UserNotFoundException이 발생한다") {
                    exception.message shouldBe "userId: $userId is not found"
                }
            }
        }
    }

    given("잔액 조회 요청이 있을 때") {

        every { userRepository.getUserById(userId) } returns Optional.of(user)

        val command = FetchBalanceCommand(userId)

        `when`("잔액 조회 작업을 실행하면") {

            val result = balanceService.getBalance(command)

            then("사용자의 잔액 정보가 반환된다") {
                result.userId shouldBe userId
//                result.amount shouldBe initialBalance
            }
        }

        given("사용자가 존재하지 않으면") {

            every { userRepository.getUserById(userId) } returns Optional.empty()

            `when`("잔액 조회 작업을 실행하면") {

                val exception =
                    shouldThrow<UserNotFoundException> {
                        balanceService.getBalance(command)
                    }

                then("UserNotFoundException이 발생한다") {
                    exception.message shouldBe "userId: $userId is not found"
                }
            }
        }
    }
})
