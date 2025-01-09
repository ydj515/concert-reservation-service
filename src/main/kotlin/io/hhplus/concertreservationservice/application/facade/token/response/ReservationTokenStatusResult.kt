package io.hhplus.concertreservationservice.application.facade.token.response

import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenStatusResponse

data class ReservationTokenStatusResult(
    val token: String,
    val status: TokenStatus,
    val userId: Long,
)

fun ReservationTokenStatusResult.toReservationTokenStatusResponse(): ReservationTokenStatusResponse {
    return ReservationTokenStatusResponse(
        status = this.status,
        userId = this.userId,
    )
}
