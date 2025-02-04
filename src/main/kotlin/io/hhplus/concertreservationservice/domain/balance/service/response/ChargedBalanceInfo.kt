package io.hhplus.concertreservationservice.domain.balance.service.response

import io.hhplus.concertreservationservice.application.facade.balance.response.ChargeBalanceResult
import io.hhplus.concertreservationservice.domain.balance.Money

data class ChargedBalanceInfo(
    val userId: Long,
    val userName: String,
    val money: Money,
)

fun ChargedBalanceInfo.toChargeBalanceResult(): ChargeBalanceResult {
    return ChargeBalanceResult(
        userId = this.userId,
        userName = this.userName,
        balance = this.money,
    )
}
