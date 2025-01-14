package io.hhplus.concertreservationservice.domain.concert.service

import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.concert.exception.ReservationNotFoundException
import io.hhplus.concertreservationservice.domain.concert.exception.SeatNotFoundException
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import io.hhplus.concertreservationservice.domain.concert.service.request.CreateReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.request.ValidReservationCommand
import io.hhplus.concertreservationservice.domain.payment.PaymentStatus
import io.hhplus.concertreservationservice.domain.payment.service.response.ProcessPaymentInfo
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import io.hhplus.concertreservationservice.domain.reservation.exception.AlreadyReservedException
import io.hhplus.concertreservationservice.domain.reservation.repository.SeatReservationRepository
import io.hhplus.concertreservationservice.domain.reservation.service.ReservationService
import io.hhplus.concertreservationservice.domain.user.User
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class ReservationServiceTest : BehaviorSpec({

    val reservationRepository = mockk<SeatReservationRepository>()
    val seatRepository = mockk<SeatRepository>()
    val reservationService = ReservationService(reservationRepository, seatRepository)

    val givenSeat =
        Seat(
            no = 1,
            scheduleSeat =
                ScheduleSeat(
                    type = SeatType.UNDEFINED,
                    price = Money(50000),
                    seatCount = 50,
                    schedule =
                        Schedule(
                            performanceDate = LocalDate.of(2024, 1, 1),
                            performanceTime = 300,
                            reservationPeriod =
                                DateRange(
                                    start = LocalDate.of(2023, 12, 1),
                                    end = LocalDate.of(2023, 12, 31),
                                ),
                            concert = Concert(title = "싸이 흠뻑쇼"),
                            place = Place(name = "상암월드컵경기장", availableSeatCount = 1000),
                        ),
                ),
        )
    given("예약 확인 요청이 있을 때") {
        val reservationId = 1L
        val reservation =
            SeatReservation(
                id = reservationId,
                user = User(id = 1L, name = "John"),
                seat = givenSeat,
                paymentId = null,
            )
        val command = ValidReservationCommand(reservationId, reservation.user.id)

        every { reservationRepository.findReservationWithLock(reservationId) } returns reservation

        `when`("유효한 예약이 있을 때") {
            val result = reservationService.validateReservation(command)

            then("예약 정보를 반환한다") {
                result.id shouldBe reservationId
            }
        }

        `when`("예약이 유효하지 않을 때") {
            every { reservationRepository.findReservationWithLock(reservationId) } returns null

            then("예약이 없다는 예외가 발생한다") {
                assertThrows<ReservationNotFoundException> {
                    reservationService.validateReservation(command)
                }
            }
        }
    }

    given("예약 완료 요청이 있을 때") {
        val reservation =
            SeatReservation(
                id = 1L,
                user = User(id = 1L, name = "John"),
                seat = givenSeat,
                paymentId = 1L,
            )
        val paymentInfo = ProcessPaymentInfo(1L, PaymentStatus.COMPLETED)

        every { reservationRepository.saveReservation(reservation) } returns reservation

        `when`("결제가 완료된 예약을 저장할 때") {
            reservationService.completeReservation(reservation, paymentInfo)

            then("예약이 저장된다") {
                verify { reservationRepository.saveReservation(reservation) }
            }
        }
    }

    given("좌석 예약 요청이 있을 때") {
        val command =
            CreateReserveSeatCommand(
                concertId = 1L,
                scheduleId = 1L,
                user = User(id = 1L, name = "John"),
                seatNo = 1,
            )

        val seat = givenSeat

        every { reservationRepository.findReservedSeatWithLock(command.seatNo, command.scheduleId) } returns null
        every { seatRepository.getSeatForReservationWithLock(any()) } returns seat
        every { reservationRepository.createReservation(any()) } returns
            SeatReservation(
                id = 1L,
                user = command.user,
                seat = seat,
                paymentId = null,
            )

        `when`("예약 요청이 유효할 때") {
            val result = reservationService.createReservationInfo(command)

            then("예약 정보가 생성된다") {
                result.seatNo shouldBe 1
            }
        }

        `when`("이미 예약된 좌석을 예약할 때") {
            val reservedSeat = SeatReservation(id = 1L, user = command.user, seat = seat, paymentId = null)

            every {
                reservationRepository.findReservedSeatWithLock(
                    command.seatNo,
                    command.scheduleId,
                )
            } returns reservedSeat

            then("이미 예약된 좌석이라는 예외가 발생한다") {
                assertThrows<AlreadyReservedException> {
                    reservationService.createReservationInfo(command)
                }
            }
        }

        `when`("존재하지 않는 좌석을 예약할 때") {
            every { reservationRepository.findReservedSeatWithLock(command.seatNo, command.scheduleId) } returns null
            every { seatRepository.getSeatForReservationWithLock(any()) } returns null

            then("좌석을 찾을 수 없다는 예외가 발생한다") {
                assertThrows<SeatNotFoundException> {
                    reservationService.createReservationInfo(command)
                }
            }
        }
    }
})
