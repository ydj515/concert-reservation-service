package io.hhplus.concertreservationservice.application.facade.payment.request

class ProcessPaymentCriteria(
    val token: String,
    val reservationId: Long,
    val amount: Long,
)
