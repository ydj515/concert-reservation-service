package io.hhplus.concertreservationservice.domain.token.extension

import io.hhplus.concertreservationservice.application.service.token.response.CreateTokenInfo
import io.hhplus.concertreservationservice.application.service.token.response.TokenStatusInfo
import io.hhplus.concertreservationservice.domain.token.ReservationToken

fun ReservationToken.toCreateTokenInfo(): CreateTokenInfo {
    return CreateTokenInfo(
        token = this.token,
        expiredAt = this.expiredAt,
        status = this.status,
        userId = this.userId,
    )
}

fun ReservationToken.toReservationToken(): TokenStatusInfo {
    return TokenStatusInfo(
        token = this.token,
        expiredAt = this.expiredAt,
        status = this.status,
        userId = this.userId,
    )
}
