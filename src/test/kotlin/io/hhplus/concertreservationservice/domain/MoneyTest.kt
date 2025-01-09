package io.hhplus.concertreservationservice.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MoneyTest : StringSpec({
    "Money 객체를 유효한 금액으로 생성할 수 있다" {
        val money = Money(1000)
        money.amount shouldBe 1000
    }

    "음수 금액으로 Money 객체를 생성하면 IllegalArgumentException이 발생한다" {
        val result =
            shouldThrow<IllegalArgumentException> {
                Money(-1000)
            }
        result.message shouldBe "0원 이상이어야 합니다."
    }

    "두 Money 객체를 더할 수 있다" {
        val money1 = Money(1000)
        val money2 = Money(2000)
        val result = money1.add(money2)

        result.amount shouldBe 3000
    }

    "두 Money 객체를 뺄 수 있다" {
        val money1 = Money(3000)
        val money2 = Money(1000)
        val result = money1.subtract(money2)

        result.amount shouldBe 2000
    }

    "잔고가 부족할 때 subtract를 호출하면 예외가 발생한다" {
        val money1 = Money(1000)
        val money2 = Money(2000)

        val result =
            shouldThrow<IllegalArgumentException> {
                money1.subtract(money2)
            }
        result.message shouldBe "잔고가 부족합니다."
    }
})
