package io.hhplus.concertreservationservice.application.consumer

import io.hhplus.concertreservationservice.common.ConsumerGroup.PAYMENT_COMPLETED_CONSUMER_GROUP
import io.hhplus.concertreservationservice.common.Topic.PAYMENT_COMPLETED_TOPIC
import io.hhplus.concertreservationservice.domain.payment.event.PayEventParser
import io.hhplus.concertreservationservice.domain.payment.service.PaymentCompletedEventService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentOutboxConsumer(
    private val paymentCompletedEventService: PaymentCompletedEventService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = [PAYMENT_COMPLETED_TOPIC], groupId = PAYMENT_COMPLETED_CONSUMER_GROUP)
    fun consume(record: ConsumerRecord<String, String>) {
        val consumedRecord = PayEventParser.parsePaymentCompletedEvent(record.value())

        // 아웃박스 상태 업데이트 (발행한 이벤트가 처리되었음을 마킹)
        paymentCompletedEventService.markOutboxCompleted(consumedRecord)

        logger.info("Consumed payment record ${record.value()}")
    }
}
