package io.hhplus.concertreservationservice.application.facade.payment

import io.hhplus.concertreservationservice.application.facade.payment.request.ProcessPaymentCriteria
import io.hhplus.concertreservationservice.application.facade.payment.response.ProcessPaymentResult
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.concert.service.request.ValidReservationCommand
import io.hhplus.concertreservationservice.domain.payment.service.PaymentService
import io.hhplus.concertreservationservice.domain.payment.service.request.ProcessPaymentCommand
import io.hhplus.concertreservationservice.domain.payment.service.response.toProcessPaymentResult
import io.hhplus.concertreservationservice.domain.reservation.service.ReservationService
import io.hhplus.concertreservationservice.domain.token.service.TokenService
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import io.hhplus.concertreservationservice.domain.user.service.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentUseCase(
    private val userService: UserService,
    private val tokenService: TokenService,
    private val paymentService: PaymentService,
    private val reservationService: ReservationService,
) {
    @Transactional
    fun processPayment(criteria: ProcessPaymentCriteria): ProcessPaymentResult {
        // 토큰 정보 가져오기
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))

        // 사용자 가져오기
        val user = userService.getUser(tokenInfo.userId)

        // 예약 검증 및 예약 정보 가져오기
        val reservation =
            reservationService.validateReservation(
                ValidReservationCommand(criteria.reservationId, user.id),
            )

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

        // 예약 상태 완료 처리
        reservationService.completeReservation(reservation, paymentInfo)

        // 사용자 잔액 갱신
        userService.updateUserBalance(user, Money(criteria.amount))

        // 토큰 삭제
        tokenService.deleteToken(criteria.token)

        return paymentInfo.toProcessPaymentResult()
    }
}
