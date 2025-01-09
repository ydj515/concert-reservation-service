package io.hhplus.concertreservationservice.application.service.balance.request

import io.hhplus.concertreservationservice.domain.Money

data class ChargeBalanceCommand(
    val userId: Long,
    val amount: Money,
)
