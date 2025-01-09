package io.hhplus.concertreservationservice.domain.token.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN

class InvalidTokenException(
    cause: Throwable? = null,
) : ServiceException("Invalid or missing $RESERVATION_QUEUE_TOKEN header", cause)
