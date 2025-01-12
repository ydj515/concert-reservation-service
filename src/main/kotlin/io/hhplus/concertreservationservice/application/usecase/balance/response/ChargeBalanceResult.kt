package io.hhplus.concertreservationservice.application.usecase.balance.response

import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.presentation.controller.balance.response.BalanceChargeResponse

data class ChargeBalanceResult(
    val userId: Long,
    val userName: String,
    val balance: Money,
)

fun ChargeBalanceResult.toBalanceChargeResponse(): BalanceChargeResponse {
    return BalanceChargeResponse(
        balance = balance.amount,
    )
}
