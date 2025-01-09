package io.hhplus.concertreservationservice.domain

import jakarta.persistence.Embeddable

@Embeddable
data class Money(val amount: Long = 0) {
    init {
        require(amount >= 0) { "0원 이상이어야 합니다." }
    }

    fun add(other: Money): Money {
        return Money(this.amount + other.amount)
    }

    fun subtract(other: Money): Money {
        require(this.amount >= other.amount) { "잔고가 부족합니다." }
        return Money(this.amount - other.amount)
    }
}
