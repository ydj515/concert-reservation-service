package io.hhplus.concertreservationservice.application.usecase.balance.request

import io.hhplus.concertreservationservice.domain.balance.Money

data class ChargeBalanceCriteria(
    val amount: Money,
    val token: String,
)
