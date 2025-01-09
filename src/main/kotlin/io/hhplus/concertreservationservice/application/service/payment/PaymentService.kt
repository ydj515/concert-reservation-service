package io.hhplus.concertreservationservice.application.service.payment

import io.hhplus.concertreservationservice.application.service.payment.request.ProcessPaymentCommand
import io.hhplus.concertreservationservice.application.service.payment.response.ProcessPaymentInfo
import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.payment.Payment
import io.hhplus.concertreservationservice.domain.payment.PaymentStatus
import io.hhplus.concertreservationservice.domain.payment.repository.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
) {
    @Transactional
    fun processPayment(command: ProcessPaymentCommand): ProcessPaymentInfo {
        val payment =
            Payment(
                totalCost = Money(amount = command.amount),
                status = PaymentStatus.COMPLETED,
                paidAt = LocalDateTime.now(),
                user = command.user,
            )

        val savedPayment = paymentRepository.savePayment(payment)

        return ProcessPaymentInfo(savedPayment.id, savedPayment.status)
    }
}
