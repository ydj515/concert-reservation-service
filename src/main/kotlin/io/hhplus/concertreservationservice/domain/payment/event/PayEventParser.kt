package io.hhplus.concertreservationservice.domain.payment.event

import io.hhplus.concertreservationservice.domain.payment.PaymentStatus
import java.util.regex.Pattern

object PayEventParser {
    fun parsePaymentCompletedEvent(input: String): PaymentCompletedEvent {
        val pattern = Pattern.compile("""PaymentCompletedEvent\(paymentId=(\d+), status=([A-Z_]+)\)""")
        val matcher = pattern.matcher(input)

        return if (matcher.find()) {
            val paymentId = matcher.group(1).toLong()
            val status = PaymentStatus.valueOf(matcher.group(2))
            PaymentCompletedEvent(paymentId, status)
        } else {
            throw Exception("Invalid PaymentCompletedEvent")
        }
    }
}
