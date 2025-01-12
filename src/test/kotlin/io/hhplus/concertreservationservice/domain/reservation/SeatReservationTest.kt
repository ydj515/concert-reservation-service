package io.hhplus.concertreservationservice.domain.reservation

import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.user.User
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class SeatReservationTest : BehaviorSpec({

    val user = User(id = 1L, name = "test user")
    val seat =
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

    val reservation =
        SeatReservation(
            user = user,
            seat = seat,
            paymentId = null,
        )

    given("예약이 생성되었을 때") {

        `when`("예약이 만료되지 않았을 때") {
            val isExpired = reservation.isExpired()

            then("예약은 만료되지 않아야 한다") {
                isExpired shouldBe false
            }
        }

        `when`("예약 상태가 RESERVED일 때") {
            val isReserved = reservation.isReserved()

            then("예약 상태는 RESERVED여야 한다") {
                isReserved shouldBe true
            }
        }

        `when`("결제가 완료되었을 때") {
            val paidId = 123L
            reservation.isCompletePayment(paidId)

            then("상태는 PAID로 변경되고 결제 ID가 설정되어야 한다") {
                reservation.status shouldBe ReservationStatus.PAID
                reservation.paymentId shouldBe paidId
            }
        }
    }
})
