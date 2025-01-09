package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.payment.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<Payment, Long>
