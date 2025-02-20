package io.hhplus.concertreservationservice.domain.payment.service

import io.hhplus.concertreservationservice.common.Topic.PAYMENT_COMPLETED_TOPIC
import io.hhplus.concertreservationservice.domain.payment.event.PaymentCompletedEvent
import io.hhplus.concertreservationservice.domain.payment.outbox.PaymentCompletedOutboxEvent
import io.hhplus.concertreservationservice.domain.payment.repository.PaymentCompletedOutboxEventRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class PaymentCompletedEventService(
    private val paymentCompletedOutBoxEventRepository: PaymentCompletedOutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    fun saveOutbox(event: PaymentCompletedOutboxEvent): PaymentCompletedOutboxEvent {
        return paymentCompletedOutBoxEventRepository.save(event)
    }

    fun sendKafka(event: PaymentCompletedEvent) {
        // 여기서 파티션을 어떻게 나눠야할까?
//        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, event.paymentId.toString(), event.toString())
        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, event.toString())
    }

    fun consumeRecord(event: PaymentCompletedEvent): PaymentCompletedOutboxEvent {
        val outboxEvent =
            paymentCompletedOutBoxEventRepository.getOutboxEvent(event.paymentId)
                ?: throw IllegalArgumentException("outboxEvent is not available")

        outboxEvent.complete()

        return paymentCompletedOutBoxEventRepository.save(outboxEvent)
    }
}
