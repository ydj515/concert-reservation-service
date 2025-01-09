package io.hhplus.concertreservationservice.domain.reservation.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class AlreadyReservedException(
    private val seatNo: Int,
    cause: Throwable? = null,
) : ServiceException("seatNo: $seatNo is already reserved", cause)
