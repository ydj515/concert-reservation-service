package io.hhplus.concertreservationservice.domain.concert.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class ReservationNotFoundException(
    private val reservationId: Long,
    cause: Throwable? = null,
) : ServiceException("reservationId: $reservationId is not found", cause)
