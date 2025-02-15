package io.hhplus.concertreservationservice.application.facade.payment.processor

import io.hhplus.concertreservationservice.application.facade.payment.PaymentContext
import io.hhplus.concertreservationservice.application.facade.payment.event.ExternalPayEvent
import io.hhplus.concertreservationservice.application.facade.payment.response.ProcessPaymentResult
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.payment.service.PaymentService
import io.hhplus.concertreservationservice.domain.payment.service.request.ProcessPaymentCommand
import io.hhplus.concertreservationservice.domain.payment.service.response.toProcessPaymentResult
import io.hhplus.concertreservationservice.domain.reservation.service.ReservationService
import io.hhplus.concertreservationservice.domain.token.service.TokenService
import io.hhplus.concertreservationservice.domain.user.service.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentProcessor(
    private val paymentService: PaymentService,
    private val userService: UserService,
    private val reservationService: ReservationService,
    private val tokenService: TokenService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun executePayment(context: PaymentContext): ProcessPaymentResult {
        val (criteria, tokenInfo, user, reservation) = context

        // 결제 처리
        val paymentInfo =
            paymentService.processPayment(
                ProcessPaymentCommand(
                    tokenInfo.token,
                    user,
                    criteria.reservationId,
                    criteria.amount,
                ),
            )

        // 사용자 잔액 갱신
        userService.updateUserBalance(user, Money(criteria.amount))

        // 예약 상태 완료 처리
        reservationService.completeReservation(reservation, paymentInfo)

        // 토큰 삭제
        tokenService.deleteToken(criteria.token)

        eventPublisher.publishEvent(ExternalPayEvent(user.name, criteria.reservationId, criteria.amount))

        return paymentInfo.toProcessPaymentResult()
    }
}
