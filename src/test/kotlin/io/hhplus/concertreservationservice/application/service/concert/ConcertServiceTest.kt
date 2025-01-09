package io.hhplus.concertreservationservice.application.service.concert

import io.hhplus.concertreservationservice.application.service.concert.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class ConcertServiceTest : BehaviorSpec({

    val seatRepository = mockk<SeatRepository>()
    val concertService = ConcertService(seatRepository)

    given("가용 좌석 조회 요청이 있을 때") {
        val command =
            SearchAvailSeatCommand(
                concertId = 1L,
                scheduleId = 1L,
                date = LocalDate.now(),
            )

        val availableSeats = createSeats()

        every { seatRepository.getAvailableSeats(command) } returns availableSeats

        `when`("getAvailableSeats 메소드가 호출되면") {
            val result = concertService.getAvailableSeats(command)

            then("가용 좌석 목록이 반환된다") {
            }
        }

        then("SeatRepository의 getAvailableSeats 메소드가 호출되었는지 확인") {
            verify { seatRepository.getAvailableSeats(command) }
        }
    }
})

fun createSeats(): List<Seat> {
    val concert1 = Concert(title = "싸이 흠뻑쇼")
    val place1 = Place(name = "상암월드컵경기장", availableSeatCount = 1000)
    val place2 = Place(name = "세종문화회관", availableSeatCount = 3000)

    val concertSchedule1 =
        Schedule(
            performanceDate = LocalDate.of(2024, 1, 1),
            performanceTime = 300,
            reservationPeriod =
                DateRange(
                    start = LocalDate.of(2023, 12, 1),
                    end = LocalDate.of(2023, 12, 31),
                ),
            concert = concert1,
            place = place1,
        )

    val concertSchedule2 =
        Schedule(
            performanceDate = LocalDate.of(2024, 2, 1),
            performanceTime = 180,
            reservationPeriod =
                DateRange(
                    start = LocalDate.of(2024, 1, 15),
                    end = LocalDate.of(2024, 1, 31),
                ),
            concert = concert1,
            place = place2,
        )

    val scheduleSeat1 =
        ScheduleSeat(
            type = SeatType.UNDEFINED,
            price = Money(50000),
            seatCount = 50,
            schedule = concertSchedule1,
        )

    val scheduleSeat2 =
        ScheduleSeat(
            type = SeatType.UNDEFINED,
            price = Money(70000),
            seatCount = 50,
            schedule = concertSchedule2,
        )
    val seats1 =
        (1..50).map { no ->
            Seat(no = no, scheduleSeat = scheduleSeat1)
        }

    val seats2 =
        (1..50).map { no ->
            Seat(no = no, scheduleSeat = scheduleSeat2)
        }
    return seats1 + seats2
}
