package io.hhplus.concertreservationservice.presentation.controller.payment.request

data class PaymentRequest(
    val reservationId: Long,
    val amount: Long,
) {
    init {
        require(amount >= 0) { "결제 금액은 0원이상이어야 합니다." }
    }
}
