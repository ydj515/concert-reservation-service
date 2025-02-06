package io.hhplus.concertreservationservice.application.facade.token.response

import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenPollingResponse

data class ReservationTokenPollingResult(
    val token: String,
    val position: Long,
)

fun ReservationTokenPollingResult.toResponse(): ReservationTokenPollingResponse {
    return ReservationTokenPollingResponse(
        token = this.token,
        position = this.position,
    )
}
