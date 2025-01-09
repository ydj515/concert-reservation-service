package io.hhplus.concertreservationservice.domain.user.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class InsufficientBalanceException(
    private val userId: Long,
    cause: Throwable? = null,
) : ServiceException("userId: $userId has not enough money", cause)
