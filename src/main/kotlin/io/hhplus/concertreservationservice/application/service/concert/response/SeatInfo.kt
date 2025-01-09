package io.hhplus.concertreservationservice.application.service.concert.response

import io.hhplus.concertreservationservice.application.facade.concert.response.SearchAvailSeatResult

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
