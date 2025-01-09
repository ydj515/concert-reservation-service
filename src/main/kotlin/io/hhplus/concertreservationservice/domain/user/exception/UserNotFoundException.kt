package io.hhplus.concertreservationservice.domain.user.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class UserNotFoundException(
    private val userId: Long,
    cause: Throwable? = null,
) : ServiceException("userId: $userId is not found", cause)
