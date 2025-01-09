package io.hhplus.concertreservationservice.domain.concert.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class ConcertNotFoundException(
    private val concertId: Long,
    cause: Throwable? = null,
) : ServiceException("concertId: $concertId is not found", cause)
