package io.hhplus.concertreservationservice.application.service.payment.request

import io.hhplus.concertreservationservice.domain.user.User

class ProcessPaymentCommand(
    val token: String,
    val user: User,
    val reservationId: Long,
    val amount: Long,
)
