package io.hhplus.concertreservationservice.application.service.token.request

data class CreateTokenCommand(
    val token: String,
    val userId: Long,
)
