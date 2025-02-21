package io.hhplus.concertreservationservice.domain.payment.event

import io.hhplus.concertreservationservice.domain.payment.PaymentStatus
import java.util.regex.Pattern

object PayEventParser {
    fun parsePaymentCompletedEvent(input: String): PaymentCompletedEvent {
        val pattern =
            Pattern.compile(
                """PaymentCompletedEvent\(paymentId=(\d+), status=([A-Z_]+), username=([^)]+), reservationId=(\d+), amount=(\d+)\)""",
            )
        val matcher = pattern.matcher(input)

        return if (matcher.find()) {
            val paymentId = matcher.group(1).toLong()
            val status = PaymentStatus.valueOf(matcher.group(2))
            val username = matcher.group(3)
            val reservationId = matcher.group(4).toLong()
            val amount = matcher.group(5).toLong()

            PaymentCompletedEvent(paymentId, status, username, reservationId, amount)
        } else {
            throw Exception("Invalid PaymentCompletedEvent")
        }
    }
}
