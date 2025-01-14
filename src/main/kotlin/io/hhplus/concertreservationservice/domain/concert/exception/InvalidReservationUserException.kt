package io.hhplus.concertreservationservice.domain.concert.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class InvalidReservationUserException(
    private val reservationId: Long,
    private val userId: Long,
    cause: Throwable? = null,
) : ServiceException("reservationId: $reservationId is invalid with userId: $userId", cause)
