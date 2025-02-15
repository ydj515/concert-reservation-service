package io.hhplus.concertreservationservice.application.facade.payment.event

import io.hhplus.concertreservationservice.application.client.ExternalPayClient
import io.hhplus.concertreservationservice.application.client.ExternalPayCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ExternalPayEventListener(
    private val externalPayClient: ExternalPayClient,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleExternalPayEvent(event: ExternalPayEvent) {
        externalPayClient.sendPayResult(ExternalPayCommand(event.userName, event.reservationId, event.amount))
    }
}
