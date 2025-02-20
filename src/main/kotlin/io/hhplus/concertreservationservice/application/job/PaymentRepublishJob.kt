package io.hhplus.concertreservationservice.application.job

import io.hhplus.concertreservationservice.common.Topic.PAYMENT_COMPLETED_TOPIC
import io.hhplus.concertreservationservice.domain.payment.event.PayEventParser
import io.hhplus.concertreservationservice.domain.payment.outbox.OutboxStatus
import io.hhplus.concertreservationservice.domain.payment.repository.PaymentCompletedOutboxEventRepository
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PaymentRepublishJob(
    private val paymentCompletedOutboxEventRepository: PaymentCompletedOutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : Job {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        logger.info("Executing ReservationExpire at ${LocalDateTime.now()} by instance: ${context.scheduler.schedulerInstanceId}")
        expireReservation(LocalDateTime.now())
    }

    fun expireReservation(currentTime: LocalDateTime) {
        val pendingEvents = paymentCompletedOutboxEventRepository.findAllForRepublish(OutboxStatus.PENDING)

        pendingEvents.forEach {
            val event = PayEventParser.parsePaymentCompletedEvent(it.payload)
            kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, event.toString())
        }
    }
}
