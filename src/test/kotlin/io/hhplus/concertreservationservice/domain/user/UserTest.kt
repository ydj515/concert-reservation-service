package io.hhplus.concertreservationservice.domain.user

import io.hhplus.concertreservationservice.domain.Money
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UserTest : StringSpec({
    "잔액 충전 테스트" {
        val user = User(name = "testUser", balance = Money(1000))
        user.chargeMoney(Money(500))
        user.balance.amount shouldBe 1500
    }

    "잔액 차감 테스트" {
        val user = User(name = "testUser", balance = Money(1000))
        user.deductMoney(Money(500))
        user.balance.amount shouldBe 500
    }

    "잔액 부족으로 차감 불가 테스트" {
        val user = User(name = "testUser", balance = Money(1000))
        shouldThrow<IllegalArgumentException> {
            user.deductMoney(Money(1500))
        }
    }
})
