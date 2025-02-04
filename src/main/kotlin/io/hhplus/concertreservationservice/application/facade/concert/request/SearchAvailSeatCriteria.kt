package io.hhplus.concertreservationservice.application.facade.concert.request

import java.time.LocalDate

data class SearchAvailSeatCriteria(
    val token: String,
    val concertId: Long,
    val scheduleId: Long,
    val date: LocalDate,
)
