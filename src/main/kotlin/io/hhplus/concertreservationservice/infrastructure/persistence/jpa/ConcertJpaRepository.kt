package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.payment.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertJpaRepository : JpaRepository<Concert, Long>
