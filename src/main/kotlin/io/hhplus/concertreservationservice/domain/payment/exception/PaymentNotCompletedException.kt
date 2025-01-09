package io.hhplus.concertreservationservice.domain.payment.exception

import io.hhplus.concertreservationservice.common.exception.ServiceException

class PaymentNotCompletedException(
    val paymentId: Long,
    cause: Throwable? = null,
) : ServiceException("paymentId: $paymentId is not completed", cause)
