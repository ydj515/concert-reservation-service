package io.hhplus.concertreservationservice.domain.payment.repository

import io.hhplus.concertreservationservice.domain.payment.outbox.OutboxStatus
import io.hhplus.concertreservationservice.domain.payment.outbox.PaymentCompletedOutboxEvent

interface PaymentCompletedOutboxEventRepository {
    fun save(event: PaymentCompletedOutboxEvent): PaymentCompletedOutboxEvent

    fun consume(event: PaymentCompletedOutboxEvent): PaymentCompletedOutboxEvent

    fun getOutboxEvent(paymentId: Long): PaymentCompletedOutboxEvent?

    fun findAllForRepublish(status: OutboxStatus): List<PaymentCompletedOutboxEvent>
}
