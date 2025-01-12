package io.hhplus.concertreservationservice.domain.token.service.request

data class CreateTokenCommand(
    val token: String,
    val userId: Long,
)
