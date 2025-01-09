package io.hhplus.concertreservationservice.domain.token.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class TokenNotFoundException(
    val token: String,
    cause: Throwable? = null,
) : ServiceException("token: $token is not found", cause)
