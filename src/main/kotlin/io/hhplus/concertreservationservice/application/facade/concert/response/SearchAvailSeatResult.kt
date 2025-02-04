package io.hhplus.concertreservationservice.application.facade.concert.response

import io.hhplus.concertreservationservice.presentation.controller.concert.response.SearchAvailSeatResponse

data class SearchAvailSeatResult(
    val concertId: Long,
    val scheduleId: Long,
    val seats: List<Int>,
    val available: Boolean,
)

fun SearchAvailSeatResult.toResponse(): SearchAvailSeatResponse {
    return SearchAvailSeatResponse(
        available = this.available,
        seats = this.seats,
    )
}
