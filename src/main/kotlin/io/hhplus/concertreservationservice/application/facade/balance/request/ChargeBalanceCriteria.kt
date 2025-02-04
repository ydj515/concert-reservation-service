package io.hhplus.concertreservationservice.application.facade.balance.request

import io.hhplus.concertreservationservice.domain.balance.Money

data class ChargeBalanceCriteria(
    val amount: Money,
    val token: String,
)
