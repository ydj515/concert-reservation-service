package io.hhplus.concertreservationservice.application.helper

interface TokenProvider {
    fun generateToken(userId: Long): String

    fun validateToken(token: String): Boolean
}
