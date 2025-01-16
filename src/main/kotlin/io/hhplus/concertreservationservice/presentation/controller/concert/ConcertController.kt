package io.hhplus.concertreservationservice.presentation.controller.concert

import io.hhplus.concertreservationservice.application.usecase.concert.ConcertUseCase
import io.hhplus.concertreservationservice.application.usecase.concert.request.SearchAvailSeatCriteria
import io.hhplus.concertreservationservice.application.usecase.concert.response.toSearchAvailResponse
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.concert.response.SearchAvailSeatResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/concert")
class ConcertController(
    private val concertUseCase: ConcertUseCase,
) {
    @Operation(
        summary = "콘서트 스케쥴 예약 날짜 조회",
        description = "콘서트의 스케쥴예약을 위해 조회합니다.",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "해당 콘서트의 스케쥴에 예약 가능한 좌석 리스트를 반환합니다.",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 토큰 요청(header 확인)이거나 concertId 혹은 scheduleId가 잘못되었습니다.",
            ),
        ],
    )
    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    fun getAvailableSeats(
        @PathVariable concertId: Long,
        @PathVariable scheduleId: Long,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") date: LocalDate,
        @RequestHeader(RESERVATION_QUEUE_TOKEN) token: String,
    ): SearchAvailSeatResponse {
        val criteria = SearchAvailSeatCriteria(token, concertId, scheduleId, date)
        val result = concertUseCase.searchAvailableSeats(criteria)
        return result.toSearchAvailResponse()
    }
}
