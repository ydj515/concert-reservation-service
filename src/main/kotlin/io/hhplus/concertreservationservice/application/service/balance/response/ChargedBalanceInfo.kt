package io.hhplus.concertreservationservice.application.service.balance.response

import io.hhplus.concertreservationservice.application.facade.balance.response.ChargeBalanceResult
import io.hhplus.concertreservationservice.domain.Money

data class ChargedBalanceInfo(
    val userId: Long,
    val userName: String,
    val amount: Money,
)

fun ChargedBalanceInfo.toChargeBalanceResult(): ChargeBalanceResult {
    return ChargeBalanceResult(
        userId = this.userId,
        userName = this.userName,
        balance = this.amount,
    )
}
