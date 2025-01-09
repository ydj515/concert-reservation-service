package io.hhplus.concertreservationservice.application.service.concert.request

import java.time.LocalDate

data class SearchAvailSeatCommand(
    val concertId: Long,
    val scheduleId: Long,
    val date: LocalDate,
)
