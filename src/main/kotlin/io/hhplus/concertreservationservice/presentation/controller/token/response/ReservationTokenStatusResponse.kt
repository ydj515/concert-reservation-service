package io.hhplus.concertreservationservice.presentation.controller.token.response

import io.hhplus.concertreservationservice.domain.token.TokenStatus

data class ReservationTokenStatusResponse(
    val status: TokenStatus,
    val userId: Long,
)
