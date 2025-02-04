package io.hhplus.concertreservationservice.application.facade.payment

import io.hhplus.concertreservationservice.application.facade.payment.request.ProcessPaymentCriteria
import io.hhplus.concertreservationservice.application.helper.TokenProvider
import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.payment.PaymentStatus
import io.hhplus.concertreservationservice.domain.payment.service.PaymentService
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import io.hhplus.concertreservationservice.domain.reservation.service.ReservationService
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.service.TokenService
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.domain.user.exception.InsufficientBalanceException
import io.hhplus.concertreservationservice.domain.user.service.UserService
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ConcertJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PaymentJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PlaceJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ReservationTokenJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleSeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatReservationJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.kotest.assertions.throwables.shouldThrow
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
class PaymentFacadeTest(
    private val paymentFacade: PaymentFacade,
    private val userService: UserService,
    private val tokenService: TokenService,
    private val paymentService: PaymentService,
    private val reservationService: ReservationService,
    private val jwtTokenProvider: TokenProvider,
    private val seatReservationJpaRepository: SeatReservationJpaRepository,
    private val seatJpaRepository: SeatJpaRepository,
    private val placeJpaRepository: PlaceJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val concertJpaRepository: ConcertJpaRepository,
    private val scheduleSeatJpaRepository: ScheduleSeatJpaRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val paymentJpaRepository: PaymentJpaRepository,
    private val tokenJpaRepository: ReservationTokenJpaRepository,
) : BehaviorSpec({
        afterEach {
            paymentJpaRepository.deleteAllInBatch()
            seatReservationJpaRepository.deleteAllInBatch()
            seatJpaRepository.deleteAllInBatch()
            scheduleSeatJpaRepository.deleteAllInBatch()
            scheduleJpaRepository.deleteAllInBatch()
            concertJpaRepository.deleteAllInBatch()
            placeJpaRepository.deleteAllInBatch()
            tokenJpaRepository.deleteAllInBatch()
            userJpaRepository.deleteAllInBatch()
        }

        given("유효한 토큰과 예약ID, 결제 금액이 주어졌을 때") {
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

            val seatReservation =
                SeatReservation(
                    user = user,
                    seat = seat,
                    paymentId = null,
                )

            val savedReservation = seatReservationJpaRepository.save(seatReservation)

            `when`("결제를 진행하면") {
                val criteria = ProcessPaymentCriteria(token, savedReservation.id, 50000)
                val result = paymentFacade.processPayment(criteria)
                then("결제에 성공한다.") {
                    val savedPayment = paymentJpaRepository.findById(result.paymentId)

                    savedPayment shouldNotBe null
                    result.status shouldBe PaymentStatus.COMPLETED
                }
            }

            `when`("10개의 스레드가 동시에 결제를 진행하면") {
                val threadCount = 10
                val latch = CountDownLatch(threadCount)
                val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)

                val criteria = ProcessPaymentCriteria(token, savedReservation.id, 50000)

                for (i in 1..threadCount) {
                    executor.submit {
                        try {
                            paymentFacade.processPayment(criteria)
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

                then("1건의 결제만 성공한다.") {
                    successfulApplications.get() shouldBe 1
                }
            }
        }

        given("유효한 토큰과 예약ID이 주어졌지만, 보유 잔고가 부족할때") {
            val user =
                userJpaRepository.save(
                    User(
                        name = "길길",
                        balance = Money(100),
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

            val seatReservation =
                SeatReservation(
                    user = user,
                    seat = seat,
                    paymentId = null,
                )

            val savedReservation = seatReservationJpaRepository.save(seatReservation)

            `when`("결제를 진행하면") {
                val criteria = ProcessPaymentCriteria(token, savedReservation.id, 50000)
                val exception =
                    shouldThrow<InsufficientBalanceException> {
                        paymentFacade.processPayment(criteria)
                    }

                then("InsufficientBalanceException이 발생한다") {
                    exception.message shouldBe "userId: ${user.id} has not enough money"
                }
            }
        }
    })
