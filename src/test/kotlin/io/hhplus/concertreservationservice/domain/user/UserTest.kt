package io.hhplus.concertreservationservice.domain.user

import io.hhplus.concertreservationservice.domain.Money
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UserTest : StringSpec({

    "500원을 충전하면 잔액이 1500원이 되어야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        user.chargeMoney(Money(500))
        user.balance.amount shouldBe 1500
    }

    "500원을 차감하면 잔액이 500원이 되어야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        user.deductMoney(Money(500))
        user.balance.amount shouldBe 500
    }

    "1500원을 차감하려고 하면 잔액 부족으로 IllegalArgumentException이 발생해야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        shouldThrow<IllegalArgumentException> {
            user.deductMoney(Money(1500))
        }
    }

    "0원을 충전하면 IllegalArgumentException이 발생해야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        shouldThrow<IllegalArgumentException> {
            user.chargeMoney(Money(0))
        }
    }

    "0원을 차감하면 IllegalArgumentException이 발생해야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        shouldThrow<IllegalArgumentException> {
            user.deductMoney(Money(0))
        }
    }

    "음수 금액을 충전하면 IllegalArgumentException이 발생해야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        shouldThrow<IllegalArgumentException> {
            user.chargeMoney(Money(-500))
        }
    }

    "음수 금액을 차감하면 IllegalArgumentException이 발생해야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        shouldThrow<IllegalArgumentException> {
            user.deductMoney(Money(-100))
        }
    }

    "충전과 차감을 여러 번 반복하면 잔액이 1300원이 되어야 한다" {
        val user = User(name = "testUser", balance = Money(1000))
        user.chargeMoney(Money(500))
        user.deductMoney(Money(200))
        user.balance.amount shouldBe 1300
    }
})
