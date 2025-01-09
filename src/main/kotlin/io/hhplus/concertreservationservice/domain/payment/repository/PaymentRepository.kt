package io.hhplus.concertreservationservice.domain.payment.repository

import io.hhplus.concertreservationservice.domain.payment.Payment

interface PaymentRepository {
    fun savePayment(payment: Payment): Payment
}
