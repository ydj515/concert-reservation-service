package io.hhplus.concertreservationservice.domain.balance.service.request

import io.hhplus.concertreservationservice.domain.balance.Money

data class ChargeBalanceCommand(
    val userId: Long,
    val money: Money,
)
