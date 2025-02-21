package io.hhplus.concertreservationservice.application.facade.payment.processor

import io.hhplus.concertreservationservice.application.facade.payment.PaymentContext
import io.hhplus.concertreservationservice.application.facade.payment.response.ProcessPaymentResult
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.payment.event.PaymentCompletedEvent
import io.hhplus.concertreservationservice.domain.payment.event.publisher.PaymentEventPublisher
import io.hhplus.concertreservationservice.domain.payment.service.PaymentService
import io.hhplus.concertreservationservice.domain.payment.service.request.ProcessPaymentCommand
import io.hhplus.concertreservationservice.domain.payment.service.response.toProcessPaymentResult
import io.hhplus.concertreservationservice.domain.reservation.service.ReservationService
import io.hhplus.concertreservationservice.domain.token.service.TokenService
import io.hhplus.concertreservationservice.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentProcessor(
    private val paymentService: PaymentService,
    private val userService: UserService,
    private val reservationService: ReservationService,
    private val tokenService: TokenService,
    private val eventPublisher: PaymentEventPublisher,
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

        // 결제되었다는 이벤트 발행
        eventPublisher.publishCompletedEvent(
            PaymentCompletedEvent(
                paymentInfo.paymentId,
                paymentInfo.status,
                user.name,
                criteria.reservationId,
                criteria.amount,
            ),
        )

        // 외부 결제 완료되었다는 api 호출 event
        return paymentInfo.toProcessPaymentResult()
    }
}
