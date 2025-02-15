package io.hhplus.concertreservationservice.application.facade.payment

import io.hhplus.concertreservationservice.application.facade.payment.processor.PaymentProcessor
import io.hhplus.concertreservationservice.application.facade.payment.request.ProcessPaymentCriteria
import io.hhplus.concertreservationservice.application.facade.payment.response.ProcessPaymentResult
import io.hhplus.concertreservationservice.domain.concert.service.request.ValidReservationCommand
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import io.hhplus.concertreservationservice.domain.reservation.service.ReservationService
import io.hhplus.concertreservationservice.domain.token.service.TokenService
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import io.hhplus.concertreservationservice.domain.token.service.response.TokenStatusInfo
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.domain.user.service.UserService
import org.springframework.stereotype.Component

@Component
class PaymentFacade(
    private val userService: UserService,
    private val tokenService: TokenService,
    private val reservationService: ReservationService,
    private val paymentProcessor: PaymentProcessor,
) {
    fun processPayment(criteria: ProcessPaymentCriteria): ProcessPaymentResult {
        val paymentContext = fetchPaymentData(criteria)

        return paymentProcessor.executePayment(paymentContext)
    }

    fun fetchPaymentData(criteria: ProcessPaymentCriteria): PaymentContext {
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))
        val user = userService.getUser(tokenInfo.userId)
        val reservation =
            reservationService.validateReservation(
                ValidReservationCommand(criteria.reservationId, user.id),
            )
        return PaymentContext(criteria, tokenInfo, user, reservation)
    }
}

data class PaymentContext(
    val criteria: ProcessPaymentCriteria,
    val tokenInfo: TokenStatusInfo,
    val user: User,
    val reservation: SeatReservation,
)
