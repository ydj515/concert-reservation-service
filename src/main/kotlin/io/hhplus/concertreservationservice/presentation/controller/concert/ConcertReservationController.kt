package io.hhplus.concertreservationservice.presentation.controller.concert

import io.hhplus.concertreservationservice.application.facade.concert.ConcertUseCase
import io.hhplus.concertreservationservice.application.facade.concert.request.SeatReserveCriteria
import io.hhplus.concertreservationservice.application.facade.concert.response.toResponse
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.hhplus.concertreservationservice.presentation.controller.concert.request.ReservationSeatRequest
import io.hhplus.concertreservationservice.presentation.controller.concert.response.ReservationSeatResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/concert")
class ConcertReservationController(
    private val concertUseCase: ConcertUseCase,
) {
    @Operation(
        summary = "콘서트 스케쥴 좌석 예약",
        description = "콘서트의 스케쥴의 좌석을 예약합니다.",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "해당 콘서트의 스케쥴에 예약을 진행합니다. 예약 상태를 반환합니다.",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 토큰 요청(header 확인)이거나 concertId 혹은 scheduleId 혹은 좌석 번호가 잘못되었습니다.",
            ),
        ],
    )
    @PostMapping("/{concertId}/schedules/{scheduleId}/reservations")
    fun reserveSeat(
        @PathVariable concertId: Long,
        @PathVariable scheduleId: Long,
        @RequestBody reservationRequest: ReservationSeatRequest,
        @RequestHeader(RESERVATION_QUEUE_TOKEN) token: String,
    ): ReservationSeatResponse {
        val criteria = SeatReserveCriteria(concertId, scheduleId, reservationRequest.seatNo, token)
        val result = concertUseCase.reserveSeat(criteria)
        return result.toResponse()
    }
}
