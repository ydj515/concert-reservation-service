package io.hhplus.concertreservationservice.domain.concert.repository

import io.hhplus.concertreservationservice.domain.concert.Schedule
import java.util.Optional

interface ScheduleRepository {
    fun getSchedule(id: Long): Optional<Schedule>
}
