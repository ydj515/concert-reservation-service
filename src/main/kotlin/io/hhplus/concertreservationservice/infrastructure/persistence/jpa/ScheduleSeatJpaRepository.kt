package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleSeatJpaRepository : JpaRepository<ScheduleSeat, Long>
