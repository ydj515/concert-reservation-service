package io.hhplus.concertreservationservice.application.usecase.balance.request

import io.hhplus.concertreservationservice.domain.Money

data class ChargeBalanceCriteria(
    val amount: Money,
    val token: String,
)
