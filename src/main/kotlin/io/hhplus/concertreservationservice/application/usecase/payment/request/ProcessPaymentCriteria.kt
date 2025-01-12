package io.hhplus.concertreservationservice.application.usecase.payment.request

class ProcessPaymentCriteria(
    val token: String,
    val reservationId: Long,
    val amount: Long,
)
