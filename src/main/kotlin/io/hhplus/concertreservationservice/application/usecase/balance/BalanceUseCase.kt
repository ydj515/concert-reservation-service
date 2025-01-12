package io.hhplus.concertreservationservice.application.usecase.balance

import io.hhplus.concertreservationservice.application.service.balance.BalanceService
import io.hhplus.concertreservationservice.application.service.balance.request.ChargeBalanceCommand
import io.hhplus.concertreservationservice.application.service.balance.request.FetchBalanceCommand
import io.hhplus.concertreservationservice.application.service.balance.response.toChargeBalanceResult
import io.hhplus.concertreservationservice.application.service.balance.response.toFetchBalanceResult
import io.hhplus.concertreservationservice.application.service.token.TokenService
import io.hhplus.concertreservationservice.application.service.token.request.TokenStatusCommand
import io.hhplus.concertreservationservice.application.usecase.balance.request.ChargeBalanceCriteria
import io.hhplus.concertreservationservice.application.usecase.balance.request.FetchBalanceCriteria
import io.hhplus.concertreservationservice.application.usecase.balance.response.ChargeBalanceResult
import io.hhplus.concertreservationservice.application.usecase.balance.response.FetchBalanceResult
import org.springframework.stereotype.Component

@Component
class BalanceUseCase(
    private val balanceService: BalanceService,
    private val tokenService: TokenService,
) {
    fun chargeBalance(criteria: ChargeBalanceCriteria): ChargeBalanceResult {
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))
        val chargedBalanceInfo = balanceService.chargeBalance(ChargeBalanceCommand(tokenInfo.userId, criteria.amount))
        return chargedBalanceInfo.toChargeBalanceResult()
    }

    fun getBalance(criteria: FetchBalanceCriteria): FetchBalanceResult {
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))
        val fetchBalanceInfo = balanceService.getBalance(FetchBalanceCommand(tokenInfo.userId))
        return fetchBalanceInfo.toFetchBalanceResult()
    }
}
