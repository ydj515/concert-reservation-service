package io.hhplus.concertreservationservice.application.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.concertreservationservice.common.ConsumerGroup.PAYMENT_COMPLETED_CONSUMER_GROUP
import io.hhplus.concertreservationservice.common.Topic.PAYMENT_COMPLETED_TOPIC
import io.hhplus.concertreservationservice.domain.payment.event.PayEventParser
import io.hhplus.concertreservationservice.domain.payment.service.PaymentCompletedEventService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentCompletedConsumer(
    private val paymentCompletedEventService: PaymentCompletedEventService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()

    @KafkaListener(topics = [PAYMENT_COMPLETED_TOPIC], groupId = PAYMENT_COMPLETED_CONSUMER_GROUP)
    fun consume(record: ConsumerRecord<String, String>) {
        val consumedRecord = PayEventParser.parsePaymentCompletedEvent(record.value())

        paymentCompletedEventService.consumeRecord(consumedRecord)

        logger.info("Consumed payment record ${record.value()}")
    }
}
