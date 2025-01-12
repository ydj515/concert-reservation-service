package io.hhplus.concertreservationservice.application.usecase.payment

import io.hhplus.concertreservationservice.application.service.concert.ReservationService
import io.hhplus.concertreservationservice.application.service.payment.PaymentService
import io.hhplus.concertreservationservice.application.service.payment.request.ProcessPaymentCommand
import io.hhplus.concertreservationservice.application.service.payment.response.toProcessPaymentResult
import io.hhplus.concertreservationservice.application.service.token.TokenService
import io.hhplus.concertreservationservice.application.service.token.request.TokenStatusCommand
import io.hhplus.concertreservationservice.application.service.user.UserService
import io.hhplus.concertreservationservice.application.usecase.payment.request.ProcessPaymentCriteria
import io.hhplus.concertreservationservice.application.usecase.payment.response.ProcessPaymentResult
import io.hhplus.concertreservationservice.domain.Money
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

        // 사용자 get
        val user = userService.getUser(tokenInfo.userId)

        // 예약 검증
        val reservation = reservationService.validateReservation(criteria.reservationId)

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
