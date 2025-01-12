package io.hhplus.concertreservationservice.domain.balance.service.response

import io.hhplus.concertreservationservice.application.usecase.balance.response.FetchBalanceResult
import io.hhplus.concertreservationservice.domain.balance.Money

data class FetchedBalanceInfo(
    val userId: Long,
    val userName: String,
    val amount: Money,
)

fun FetchedBalanceInfo.toFetchBalanceResult(): FetchBalanceResult {
    return FetchBalanceResult(
        userId = this.userId,
        userName = this.userName,
        balance = this.amount,
    )
}
