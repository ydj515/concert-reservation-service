package io.hhplus.concertreservationservice.domain.reservation

import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.payment.Payment
import io.hhplus.concertreservationservice.domain.user.User
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

@Entity
@Comment("좌석 예약 현황")
class SeatReservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Enumerated(EnumType.STRING)
    @Comment("예약 상태")
    val status: ReservationStatus = ReservationStatus.RESERVED,
    @Comment("좌석 예약일시")
    val reservedAt: LocalDateTime = LocalDateTime.now(),
    @Comment("결제전 좌석예약 임시 만료일시")
    val reservationExpiredAt: LocalDateTime = reservedAt.plusMinutes(5),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    val seat: Seat,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    val payment: Payment,
) {
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(reservationExpiredAt)
}
