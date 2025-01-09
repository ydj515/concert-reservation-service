package io.hhplus.concertreservationservice.domain.concert.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class SeatNotFoundException(
    cause: Throwable? = null,
) : ServiceException("seat is not found", cause)
