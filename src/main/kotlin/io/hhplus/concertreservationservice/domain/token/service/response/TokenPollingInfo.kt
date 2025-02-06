package io.hhplus.concertreservationservice.domain.token.service.response

import io.hhplus.concertreservationservice.application.facade.token.response.ReservationTokenPollingResult

class TokenPollingInfo(
    val token: String,
    val position: Long,
)

fun TokenPollingInfo.toResult(): ReservationTokenPollingResult {
    return ReservationTokenPollingResult(
        token = this.token,
        position = this.position,
    )
}
