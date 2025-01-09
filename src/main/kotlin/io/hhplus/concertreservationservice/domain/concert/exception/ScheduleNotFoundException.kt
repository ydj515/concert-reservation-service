package io.hhplus.concertreservationservice.domain.concert.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class ScheduleNotFoundException(
    private val scheduleId: Long,
    cause: Throwable? = null,
) : ServiceException("scheduleId: $scheduleId is not found", cause)
