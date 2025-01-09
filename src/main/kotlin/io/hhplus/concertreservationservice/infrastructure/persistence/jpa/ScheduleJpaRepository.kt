package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.concert.Schedule
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleJpaRepository : JpaRepository<Schedule, Long>
