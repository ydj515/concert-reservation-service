package io.hhplus.concertreservationservice.application.usecase.concert.response

import io.hhplus.concertreservationservice.presentation.controller.concert.response.SearchAvailSeatResponse

data class SearchAvailSeatResult(
    val concertId: Long,
    val scheduleId: Long,
    val seats: List<Int>,
    val available: Boolean,
)

fun SearchAvailSeatResult.toSearchAvailResponse(): SearchAvailSeatResponse {
    return SearchAvailSeatResponse(
        available = this.available,
        seats = this.seats,
    )
}
