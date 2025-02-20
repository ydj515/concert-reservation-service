package io.hhplus.concertreservationservice.application.listener.payment

import io.hhplus.concertreservationservice.domain.payment.event.PaymentCompletedEvent
import io.hhplus.concertreservationservice.domain.payment.outbox.PaymentCompletedOutboxEvent
import io.hhplus.concertreservationservice.domain.payment.service.PaymentCompletedEventService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
    private val paymentCompletedEventService: PaymentCompletedEventService,
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun saveOutbox(event: PaymentCompletedEvent) {
        val outboxEvent =
            PaymentCompletedOutboxEvent(
                payload = event.toString(),
                paymentId = event.paymentId,
            )

        paymentCompletedEventService.saveOutbox(outboxEvent)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendKafka(event: PaymentCompletedEvent) {
        paymentCompletedEventService.sendKafka(event)
    }
}
