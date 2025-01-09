package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.domain.payment.Payment
import io.hhplus.concertreservationservice.domain.payment.repository.PaymentRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PaymentJpaRepository
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(
    private val paymentJpaRepository: PaymentJpaRepository,
) : PaymentRepository {
    override fun savePayment(payment: Payment): Payment {
        return paymentJpaRepository.save(payment)
    }
}
