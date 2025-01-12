package io.hhplus.concertreservationservice.application.service.user

import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.domain.user.exception.InsufficientBalanceException
import io.hhplus.concertreservationservice.domain.user.exception.UserNotFoundException
import io.hhplus.concertreservationservice.domain.user.repository.UserRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val userService = UserService(userRepository)

    given("사용자 잔액 업데이트 요청이 있을 때") {
        val user = User(id = 1L, name = "길길", balance = Money(100))
        val money = Money(50)

        every { userRepository.save(any()) } returns user

        `when`("사용자가 충분한 잔액을 가지고 있을 때") {
            userService.updateUserBalance(user, money)

            then("사용자의 잔액이 차감되어야 한다") {
                assertEquals(50, user.balance.amount)
            }

            then("사용자 정보를 저장하는 메서드가 호출되어야 한다") {
                verify(exactly = 1) { userRepository.save(user) }
            }
        }

        `when`("사용자가 충분한 잔액을 가지고 있지 않을 때") {
            val insufficientMoney = Money(200)

            then("InsufficientBalanceException이 발생해야 한다") {
                assertFailsWith<InsufficientBalanceException> {
                    userService.updateUserBalance(user, insufficientMoney)
                }
            }
        }
    }

    given("사용자 조회 요청이 있을 때") {
        val userId = 1L
        val user = User(id = userId, name = "길길", balance = Money(100))

        every { userRepository.getUser(userId) } returns user

        `when`("사용자 정보를 조회하면") {
            val result = userService.getUser(userId)

            then("사용자의 정보가 반환되어야 한다") {
                assertEquals(user.id, result.id)
                assertEquals(user.name, result.name)
            }

            then("사용자 조회 메서드가 호출되어야 한다") {
                verify(exactly = 1) { userRepository.getUser(userId) }
            }
        }

        `when`("존재하지 않는 사용자 정보를 조회하면") {
            every { userRepository.getUser(userId) } returns null

            then("UserNotFoundException이 발생해야 한다") {
                assertFailsWith<UserNotFoundException> {
                    userService.getUser(userId)
                }
            }
        }
    }
})
