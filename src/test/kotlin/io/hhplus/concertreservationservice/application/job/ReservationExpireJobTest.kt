package io.hhplus.concertreservationservice.application.job

import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.reservation.ReservationStatus
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ConcertJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PlaceJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleSeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatReservationJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("integration-test")
@SpringBootTest
class ReservationExpireJobTest(
    private val reservationExpireJob: ReservationExpireJob,
    private val reservationRepository: SeatReservationJpaRepository,
    private val seatJpaRepository: SeatJpaRepository,
    private val placeJpaRepository: PlaceJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val concertJpaRepository: ConcertJpaRepository,
    private val scheduleSeatJpaRepository: ScheduleSeatJpaRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository,
) : BehaviorSpec({

        afterEach {
            reservationRepository.deleteAll()
            seatJpaRepository.deleteAll()
            scheduleSeatJpaRepository.deleteAll()
            scheduleJpaRepository.deleteAll()
            concertJpaRepository.deleteAll()
            placeJpaRepository.deleteAll()
            userJpaRepository.deleteAll()
        }

        given("만료된 예약과 활성 상태 예약이 존재할 때") {
            val users = mutableListOf<User>()
            for (i in 1..100) {
                users.add(User(id = i.toLong(), name = "test$i"))
            }
            val savedUser = userJpaRepository.saveAll(users)

            val place1 = Place(name = "상암월드컵경기장", availableSeatCount = 1000)
            val place2 = Place(name = "세종문화회관", availableSeatCount = 3000)
            placeJpaRepository.saveAll(listOf(place1, place2))

            val concert1 = Concert(title = "싸이 흠뻑쇼")
            val concert2 = Concert(title = "성시경 콘서트")
            val concert3 = Concert(title = "아이유 콘서트")
            concertJpaRepository.saveAll(listOf(concert1, concert2, concert3))

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
            scheduleJpaRepository.saveAll(listOf(concertSchedule1))

            val scheduleSeat1 =
                ScheduleSeat(
                    type = SeatType.UNDEFINED,
                    price = Money(50000),
                    seatCount = 50,
                    schedule = concertSchedule1,
                )
            scheduleSeatJpaRepository.saveAll(listOf(scheduleSeat1))

            val seats1 = (1..50).map { no -> Seat(no = no, scheduleSeat = scheduleSeat1) }
            seatJpaRepository.saveAll(seats1)

            val expiredReservation =
                SeatReservation(
                    status = ReservationStatus.RESERVED,
                    user = savedUser[0],
                    seat = seats1[0],
                    paymentId = null,
                    reservationExpiredAt = LocalDateTime.now().minusDays(1),
                )
            val activeReservation =
                SeatReservation(
                    status = ReservationStatus.RESERVED,
                    user = savedUser[1],
                    seat = seats1[1],
                    paymentId = null,
                    reservationExpiredAt = LocalDateTime.now().plusDays(1),
                )
            val reservations = listOf(expiredReservation, activeReservation)

            reservationRepository.saveAll(reservations)
            `when`("예약 만료 작업을 실행하면") {
                reservationExpireJob.expireReservation(LocalDateTime.now())

                then("만료된 예약이 삭제된다") {
                    val foundReservations = reservationRepository.findAll()
                    foundReservations.size shouldBe reservations.size - 1
//                foundReservations.none { it.id == reservations[0].id } shouldBe true
                }
            }
        }
    })
