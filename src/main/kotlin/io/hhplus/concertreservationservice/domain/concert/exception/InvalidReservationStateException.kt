package io.hhplus.concertreservationservice.domain.concert.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class InvalidReservationStateException(
    private val reservationId: Long,
    cause: Throwable? = null,
) : ServiceException("reservationId: $reservationId is invalid status for pay", cause)
