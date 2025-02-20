package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.domain.payment.outbox.OutboxStatus
import io.hhplus.concertreservationservice.domain.payment.outbox.PaymentCompletedOutboxEvent
import io.hhplus.concertreservationservice.domain.payment.repository.PaymentCompletedOutboxEventRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PaymentCompletedOutboxEventJpaRepository
import org.springframework.stereotype.Repository

@Repository
class PaymentCompletedCompletedOutboxEventRepositoryImpl(
    private val paymentCompletedOutboxEventJpaRepository: PaymentCompletedOutboxEventJpaRepository,
) : PaymentCompletedOutboxEventRepository {
    override fun save(event: PaymentCompletedOutboxEvent): PaymentCompletedOutboxEvent {
        return paymentCompletedOutboxEventJpaRepository.save(event)
    }

    override fun consume(event: PaymentCompletedOutboxEvent): PaymentCompletedOutboxEvent {
        return paymentCompletedOutboxEventJpaRepository.save(event)
    }

    override fun getOutboxEvent(paymentId: Long): PaymentCompletedOutboxEvent? {
        return paymentCompletedOutboxEventJpaRepository.findByPaymentId((paymentId))
    }

    override fun findAllForRepublish(status: OutboxStatus): List<PaymentCompletedOutboxEvent> {
        return paymentCompletedOutboxEventJpaRepository.findByStatus(status)
    }
}
