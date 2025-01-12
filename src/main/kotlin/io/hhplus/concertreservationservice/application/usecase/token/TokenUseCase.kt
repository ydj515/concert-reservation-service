package io.hhplus.concertreservationservice.application.usecase.token

import io.hhplus.concertreservationservice.application.service.token.TokenService
import io.hhplus.concertreservationservice.application.service.token.request.CreateTokenCommand
import io.hhplus.concertreservationservice.application.service.token.request.TokenStatusCommand
import io.hhplus.concertreservationservice.application.service.token.response.toCreateReservationTokenResult
import io.hhplus.concertreservationservice.application.service.token.response.toReservationTokenStatusResult
import io.hhplus.concertreservationservice.application.service.user.UserService
import io.hhplus.concertreservationservice.application.usecase.token.request.CreateReservationTokenCriteria
import io.hhplus.concertreservationservice.application.usecase.token.request.ReservationTokenStatusCriteria
import io.hhplus.concertreservationservice.application.usecase.token.response.CreateReservationTokenResult
import io.hhplus.concertreservationservice.application.usecase.token.response.ReservationTokenStatusResult
import io.hhplus.concertreservationservice.infrastructure.TokenProvider
import org.springframework.stereotype.Component

@Component
class TokenUseCase(
    private val tokenProvider: TokenProvider,
    private val tokenService: TokenService,
    private val userService: UserService,
) {
    fun createToken(criteria: CreateReservationTokenCriteria): CreateReservationTokenResult {
        val user = userService.getUser(criteria.userId)
        val token = tokenProvider.generateToken(criteria.userId)
        val tokenInfo = tokenService.saveToken(CreateTokenCommand(token, criteria.userId))
        return tokenInfo.toCreateReservationTokenResult()
    }

    fun getTokenStatus(criteria: ReservationTokenStatusCriteria): ReservationTokenStatusResult {
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))
        return tokenInfo.toReservationTokenStatusResult()
    }
}
