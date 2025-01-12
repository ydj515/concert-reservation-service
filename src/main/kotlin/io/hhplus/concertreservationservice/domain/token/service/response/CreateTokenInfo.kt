package io.hhplus.concertreservationservice.domain.token.service.response

import io.hhplus.concertreservationservice.application.usecase.token.response.CreateReservationTokenResult
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import java.time.LocalDateTime

data class CreateTokenInfo(
    val token: String,
    val expiredAt: LocalDateTime,
    val status: TokenStatus,
    val userId: Long,
)

fun CreateTokenInfo.toCreateReservationTokenResult(): CreateReservationTokenResult {
    return CreateReservationTokenResult(
        userId = this.userId,
        token = this.token,
    )
}
