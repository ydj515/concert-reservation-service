package io.hhplus.concertreservationservice.application.usecase.balance.response

import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.presentation.controller.balance.response.BalanceFetchResponse

data class FetchBalanceResult(
    val userId: Long,
    val userName: String,
    val balance: Money,
)

fun FetchBalanceResult.toFetchBalanceResponse(): BalanceFetchResponse {
    return BalanceFetchResponse(
        balance = balance.amount,
    )
}
