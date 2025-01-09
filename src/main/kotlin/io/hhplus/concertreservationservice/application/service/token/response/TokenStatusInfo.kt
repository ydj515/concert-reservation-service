package io.hhplus.concertreservationservice.application.service.token.response

import io.hhplus.concertreservationservice.application.facade.token.response.ReservationTokenStatusResult
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import java.time.LocalDateTime

class TokenStatusInfo(
    val token: String,
    val expiredAt: LocalDateTime,
    val status: TokenStatus,
    val userId: Long,
)

fun TokenStatusInfo.toReservationTokenStatusResult(): ReservationTokenStatusResult {
    return ReservationTokenStatusResult(
        token = this.token,
        userId = this.userId,
        status = this.status,
    )
}
