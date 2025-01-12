package io.hhplus.concertreservationservice.application.service.payment

import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.payment.Payment
import io.hhplus.concertreservationservice.domain.payment.PaymentStatus
import io.hhplus.concertreservationservice.domain.payment.repository.PaymentRepository
import io.hhplus.concertreservationservice.domain.payment.service.PaymentService
import io.hhplus.concertreservationservice.domain.payment.service.request.ProcessPaymentCommand
import io.hhplus.concertreservationservice.domain.user.User
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import kotlin.test.assertEquals

class PaymentServiceTest : BehaviorSpec({

    val paymentRepository = mockk<PaymentRepository>()
    val paymentService = PaymentService(paymentRepository)

    given("결제 요청이 들어왔을 때") {
        val command =
            ProcessPaymentCommand(
                reservationId = 1L,
                token = "TOKEN",
                amount = 100,
                user = User(id = 1L, name = "길길"),
            )

        val savedPayment =
            Payment(
                totalCost = Money(amount = 100),
                status = PaymentStatus.COMPLETED,
                paidAt = LocalDateTime.now(),
                user = command.user,
            )

        every { paymentRepository.savePayment(any()) } returns savedPayment

        `when`("결제 처리를 하면") {
            val result = paymentService.processPayment(command)

            then("결제 정보가 정상적으로 반환되어야 한다") {
                assertEquals(savedPayment.id, result.paymentId)
                assertEquals(savedPayment.status, result.status)
            }

            then("결제 정보가 저장되어야 한다") {
                verify(exactly = 1) { paymentRepository.savePayment(any()) }
            }
        }
    }
})
