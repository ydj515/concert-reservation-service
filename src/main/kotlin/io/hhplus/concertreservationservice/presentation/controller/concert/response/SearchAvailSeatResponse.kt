package io.hhplus.concertreservationservice.presentation.controller.concert.response

data class SearchAvailSeatResponse(
    val available: Boolean,
    val seats: List<Int>,
)
