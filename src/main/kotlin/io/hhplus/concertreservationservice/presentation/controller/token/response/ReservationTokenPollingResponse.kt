package io.hhplus.concertreservationservice.presentation.controller.token.response

data class ReservationTokenPollingResponse(
    val token: String,
    val position: Long,
)
