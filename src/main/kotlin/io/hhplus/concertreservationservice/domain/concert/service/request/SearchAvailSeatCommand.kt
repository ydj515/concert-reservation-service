package io.hhplus.concertreservationservice.domain.concert.service.request

import java.time.LocalDate

data class SearchAvailSeatCommand(
    val concertId: Long,
    val scheduleId: Long,
    val date: LocalDate,
)
