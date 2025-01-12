package io.hhplus.concertreservationservice.domain.concert.service.response

import io.hhplus.concertreservationservice.application.usecase.concert.response.SearchAvailSeatResult

data class SeatInfo(
    val no: Int,
)

fun List<SeatInfo>.toAvailSeatResult(
    concertId: Long,
    scheduleId: Long,
): SearchAvailSeatResult {
    return SearchAvailSeatResult(
        concertId = concertId,
        scheduleId = scheduleId,
        seats = this.map { it.no },
        available = this.isNotEmpty(),
    )
}
