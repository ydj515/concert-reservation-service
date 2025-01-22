package io.hhplus.concertreservationservice.application.usecase.token.response

import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenCreateResponse

data class CreateReservationTokenResult(
    val userId: Long,
    val token: String,
)

fun CreateReservationTokenResult.toResponse(): ReservationTokenCreateResponse {
    return ReservationTokenCreateResponse(
        token = this.token,
    )
}
