package io.hhplus.concertreservationservice.domain.token.extension

import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.service.response.CreateTokenInfo
import io.hhplus.concertreservationservice.domain.token.service.response.TokenStatusInfo

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
