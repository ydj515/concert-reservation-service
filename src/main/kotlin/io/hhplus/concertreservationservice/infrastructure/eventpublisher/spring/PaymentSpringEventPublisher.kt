package io.hhplus.concertreservationservice.infrastructure.eventpublisher.spring

import io.hhplus.concertreservationservice.domain.payment.event.ExternalPayEvent
import io.hhplus.concertreservationservice.domain.payment.event.PaymentCompletedEvent
import io.hhplus.concertreservationservice.domain.payment.event.publisher.PaymentEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class PaymentSpringEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : PaymentEventPublisher {
    override fun publishCompletedEvent(event: PaymentCompletedEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun publishExternalPayEvent(event: ExternalPayEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
