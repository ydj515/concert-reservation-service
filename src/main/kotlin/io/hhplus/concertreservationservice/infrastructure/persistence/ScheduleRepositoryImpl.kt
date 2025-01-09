package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.repository.ScheduleRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleJpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ScheduleRepositoryImpl(
    private val scheduleJpaRepository: ScheduleJpaRepository,
) : ScheduleRepository {
    override fun getSchedule(id: Long): Optional<Schedule> {
        return scheduleJpaRepository.findById(id)
    }
}
