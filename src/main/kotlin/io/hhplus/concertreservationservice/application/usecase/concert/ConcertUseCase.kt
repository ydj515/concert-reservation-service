package io.hhplus.concertreservationservice.application.usecase.concert

import io.hhplus.concertreservationservice.application.service.concert.ConcertService
import io.hhplus.concertreservationservice.application.service.concert.ReservationService
import io.hhplus.concertreservationservice.application.service.concert.request.CreateReserveSeatCommand
import io.hhplus.concertreservationservice.application.service.concert.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.application.service.concert.response.toAvailSeatResult
import io.hhplus.concertreservationservice.application.service.concert.response.toSeatReserveResult
import io.hhplus.concertreservationservice.application.service.token.TokenService
import io.hhplus.concertreservationservice.application.service.token.request.TokenStatusCommand
import io.hhplus.concertreservationservice.application.service.user.UserService
import io.hhplus.concertreservationservice.application.usecase.concert.request.SearchAvailSeatCriteria
import io.hhplus.concertreservationservice.application.usecase.concert.request.SeatReserveCriteria
import io.hhplus.concertreservationservice.application.usecase.concert.response.SearchAvailSeatResult
import io.hhplus.concertreservationservice.application.usecase.concert.response.SeatReserveResult
import org.springframework.stereotype.Component

@Component
class ConcertUseCase(
    private val concertService: ConcertService,
    private val userService: UserService,
    private val tokenService: TokenService,
    private val reservationService: ReservationService,
) {
    fun searchAvailableSeats(criteria: SearchAvailSeatCriteria): SearchAvailSeatResult {
        val seatInfos =
            concertService.getAvailableSeats(
                SearchAvailSeatCommand(
                    criteria.concertId,
                    criteria.scheduleId,
                    criteria.date,
                ),
            )
        return seatInfos.toAvailSeatResult(criteria.concertId, criteria.scheduleId)
    }

    fun reserveSeat(criteria: SeatReserveCriteria): SeatReserveResult {
        // 사용자 get
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))
        val user = userService.getUser(tokenInfo.userId)

        // 예약
        val reservedSeatInfo =
            reservationService.createReservationInfo(
                CreateReserveSeatCommand(criteria.concertId, criteria.scheduleId, criteria.seatNo, user),
            )
        return reservedSeatInfo.toSeatReserveResult()
    }
}
