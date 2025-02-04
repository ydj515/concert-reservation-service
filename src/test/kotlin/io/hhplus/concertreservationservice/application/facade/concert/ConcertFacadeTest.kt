package io.hhplus.concertreservationservice.application.facade.concert

import io.hhplus.concertreservationservice.application.facade.concert.request.SeatReserveCriteria
import io.hhplus.concertreservationservice.application.helper.TokenProvider
import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ConcertJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PlaceJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleSeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatReservationJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@ActiveProfiles("integration-test")
@SpringBootTest
class ConcertFacadeTest(
    private val concertFacade: ConcertFacade,
    private val jwtTokenProvider: TokenProvider,
    private val seatReservationJpaRepository: SeatReservationJpaRepository,
    private val seatJpaRepository: SeatJpaRepository,
    private val placeJpaRepository: PlaceJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val concertJpaRepository: ConcertJpaRepository,
    private val scheduleSeatJpaRepository: ScheduleSeatJpaRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val tokenJpaRepository: ReservationTokenJpaRepository,
) : BehaviorSpec({

        afterEach {
            seatReservationJpaRepository.deleteAllInBatch()
            seatJpaRepository.deleteAllInBatch()
            scheduleSeatJpaRepository.deleteAllInBatch()
            scheduleJpaRepository.deleteAllInBatch()
            concertJpaRepository.deleteAllInBatch()
            placeJpaRepository.deleteAllInBatch()
            tokenJpaRepository.deleteAllInBatch()
            userJpaRepository.deleteAllInBatch()
        }

        given("유효한 토큰과 예약할 좌석 정보가 주어졌을 때") {
            val user =
                userJpaRepository.save(
                    User(
                        name = "길길",
                        balance = Money(50000),
                    ),
                )
            val seatNo = 1

            val place = placeJpaRepository.save(Place(name = "상암월드컵경기장", availableSeatCount = 1000))
            val concert = concertJpaRepository.save(Concert(title = "싸이 흠뻑쇼"))
            val schedule =
                scheduleJpaRepository.save(
                    Schedule(
                        performanceDate = LocalDate.of(2024, 1, 1),
                        performanceTime = 300,
                        reservationPeriod =
                            DateRange(
                                start = LocalDate.of(2023, 12, 1),
                                end = LocalDate.of(2023, 12, 31),
                            ),
                        concert = concert,
                        place = place,
                    ),
                )

            val scheduleSeat =
                scheduleSeatJpaRepository.save(
                    ScheduleSeat(
                        type = SeatType.UNDEFINED,
                        price = Money(50000),
                        seatCount = 50,
                        schedule = schedule,
                    ),
                )

            // 예약을 시작할 수 있도록 설정
            val seat = seatJpaRepository.save(Seat(no = seatNo, scheduleSeat = scheduleSeat))

            val token = jwtTokenProvider.generateToken(user.id)
            tokenJpaRepository.save(
                ReservationToken(
                    expiredAt = LocalDateTime.now(),
                    token = token,
                    userId = user.id,
                ),
            )
            `when`("해당 좌석을 예약한다면") {
                val seatReserveCriteria = SeatReserveCriteria(concert.id, schedule.id, seatNo, token)
                val result = concertFacade.reserveSeat(seatReserveCriteria)
                then("예약에 성공한다") {
                    val reservedSeat = seatReservationJpaRepository.findById(result.reservationId)
                    reservedSeat shouldNotBe null
                    result.seatNo shouldBe seatNo
                }
            }

            `when`("10개의 스레드가 동시에 해당 좌석을 예약한다면") {
                val threadCount = 10
                val latch = CountDownLatch(threadCount)
                val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)
                val seatReserveCriteria = SeatReserveCriteria(concert.id, schedule.id, seatNo, token)

                for (i in 1..threadCount) {
                    executor.submit {
                        try {
                            concertFacade.reserveSeat(seatReserveCriteria)
                            successfulApplications.incrementAndGet()
                        } catch (e: Exception) {
                            failApplications.incrementAndGet()
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()
                executor.shutdown()

                then("1건의 예약만 성공한다.") {
                    successfulApplications.get() shouldBe 1
                }
            }
        }
    })
