package io.hhplus.concertreservationservice.domain.user

import jakarta.persistence.Embeddable

@Embeddable
data class Money(val amount: Long = 0) {
    init {
        require(amount >= 0) { "Amount must be non-negative" }
    }
}
