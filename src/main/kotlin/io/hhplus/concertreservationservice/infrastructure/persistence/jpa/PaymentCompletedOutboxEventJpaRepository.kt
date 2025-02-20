package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.payment.outbox.OutboxStatus
import io.hhplus.concertreservationservice.domain.payment.outbox.PaymentCompletedOutboxEvent
import org.springframework.data.jpa.repository.JpaRepository


interface PaymentCompletedOutboxEventJpaRepository : JpaRepository<PaymentCompletedOutboxEvent, Long> {
    fun findByPaymentId(paymentId: Long): PaymentCompletedOutboxEvent?
    fun findByStatus(status: OutboxStatus): List<PaymentCompletedOutboxEvent>
}
