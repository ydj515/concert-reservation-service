package io.hhplus.concertreservationservice

import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ConcertJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PlaceJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleSeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Profile("!integration-test")
class InitializeDataLoader(
    private val userRepository: UserJpaRepository,
    private val placeRepository: PlaceJpaRepository,
    private val concertRepository: ConcertJpaRepository,
    private val scheduleRepository: ScheduleJpaRepository,
    private val scheduleSeatRepository: ScheduleSeatJpaRepository,
    private val seatRepository: SeatJpaRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        // user
        val users = mutableListOf<User>()
        for (i in 1..100) {
            users.add(
                User(
                    id = i.toLong(),
                    name = "test$i",
                ),
            )
        }

        userRepository.saveAll(users)

        // place
        val place1 = Place(name = "상암월드컵경기장", availableSeatCount = 1000)
        val place2 = Place(name = "세종문화회관", availableSeatCount = 3000)
        placeRepository.saveAll(listOf(place1, place2))

        // concert
        val concert1 = Concert(title = "싸이 흠뻑쇼")
        val concert2 = Concert(title = "성시경 콘서트")
        val concert3 = Concert(title = "아이유 콘서트")
        concertRepository.saveAll(listOf(concert1, concert2, concert3))

        // concertSchedule
        // 1. concert1 + place1: 공연일 2021-01-01, 예약 기간 2020-12-01 ~ 2020-12-31
        // 2. concert1 + place2: 공연일 2021-02-01, 예약 기간 2021-01-15 ~ 2021-01-31
        // 3. concert2 + place1: 공연일 2021-03-01, 예약 기간 2021-02-01 ~ 2021-02-28
        // 4. concert2 + place2: 공연일 2021-04-01, 예약 기간 2021-03-01 ~ 2021-03-31
        // 5. concert3 + place1: 공연일 2021-05-01, 예약 기간 2021-04-01 ~ 2021-04-30
        // 6. concert3 + place2: 공연일 2021-06-01, 예약 기간 2021-05-01 ~ 2021-05-31
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

        val concertSchedule3 =
            Schedule(
                performanceDate = LocalDate.of(2024, 3, 1),
                performanceTime = 240,
                reservationPeriod =
                    DateRange(
                        start = LocalDate.of(2024, 2, 1),
                        end = LocalDate.of(2024, 2, 28),
                    ),
                concert = concert2,
                place = place1,
            )

        val concertSchedule4 =
            Schedule(
                performanceDate = LocalDate.of(2024, 4, 1),
                performanceTime = 200,
                reservationPeriod =
                    DateRange(
                        start = LocalDate.of(2024, 3, 1),
                        end = LocalDate.of(2024, 3, 31),
                    ),
                concert = concert2,
                place = place2,
            )

        val concertSchedule5 =
            Schedule(
                performanceDate = LocalDate.of(2024, 5, 1),
                performanceTime = 360,
                reservationPeriod =
                    DateRange(
                        start = LocalDate.of(2024, 4, 1),
                        end = LocalDate.of(2024, 4, 30),
                    ),
                concert = concert3,
                place = place1,
            )

        val concertSchedule6 =
            Schedule(
                performanceDate = LocalDate.of(2024, 6, 1),
                performanceTime = 150,
                reservationPeriod =
                    DateRange(
                        start = LocalDate.of(2024, 5, 1),
                        end = LocalDate.of(2024, 5, 31),
                    ),
                concert = concert3,
                place = place2,
            )

        scheduleRepository.saveAll(
            listOf(
                concertSchedule1,
                concertSchedule2,
                concertSchedule3,
                concertSchedule4,
                concertSchedule5,
                concertSchedule6,
            ),
        )

        // ScheduleSeat
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

        val scheduleSeat3 =
            ScheduleSeat(
                type = SeatType.UNDEFINED,
                price = Money(70000),
                seatCount = 50,
                schedule = concertSchedule3,
            )

        val scheduleSeat4 =
            ScheduleSeat(
                type = SeatType.UNDEFINED,
                price = Money(70000),
                seatCount = 50,
                schedule = concertSchedule4,
            )

        val scheduleSeat5 =
            ScheduleSeat(
                type = SeatType.UNDEFINED,
                price = Money(70000),
                seatCount = 50,
                schedule = concertSchedule5,
            )

        val scheduleSeat6 =
            ScheduleSeat(
                type = SeatType.UNDEFINED,
                price = Money(70000),
                seatCount = 50,
                schedule = concertSchedule6,
            )

        scheduleSeatRepository.saveAll(
            listOf(
                scheduleSeat1,
                scheduleSeat2,
                scheduleSeat3,
                scheduleSeat4,
                scheduleSeat5,
                scheduleSeat6,
            ),
        )

        // seat

        val seats1 =
            (1..50).map { no ->
                Seat(no = no, scheduleSeat = scheduleSeat1)
            }

        val seats2 =
            (1..50).map { no ->
                Seat(no = no, scheduleSeat = scheduleSeat2)
            }

        val seats3 =
            (1..50).map { no ->
                Seat(no = no, scheduleSeat = scheduleSeat3)
            }

        val seats4 =
            (1..50).map { no ->
                Seat(no = no, scheduleSeat = scheduleSeat4)
            }

        val seats5 =
            (1..50).map { no ->
                Seat(no = no, scheduleSeat = scheduleSeat5)
            }

        val seats6 =
            (1..50).map { no ->
                Seat(no = no, scheduleSeat = scheduleSeat6)
            }

        seatRepository.saveAll(seats1 + seats2 + seats3 + seats4 + seats5 + seats6)
    }
}
