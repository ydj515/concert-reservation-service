package io.hhplus.concertreservationservice.application.facade.token.response

import io.hhplus.concertreservationservice.presentation.controller.token.response.ReservationTokenCreateResponse

data class CreateReservationTokenResult(
    val userId: Long,
    val token: String,
)

fun CreateReservationTokenResult.toReservationTokenCreateResponse(): ReservationTokenCreateResponse {
    return ReservationTokenCreateResponse(
        token = this.token,
    )
}
