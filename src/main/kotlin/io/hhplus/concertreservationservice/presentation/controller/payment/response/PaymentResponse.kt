package io.hhplus.concertreservationservice.presentation.controller.payment.response

import io.hhplus.concertreservationservice.domain.payment.PaymentStatus

data class PaymentResponse(
    val status: PaymentStatus,
)
