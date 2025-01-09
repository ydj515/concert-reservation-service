package io.hhplus.concertreservationservice.domain.concert

import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Comment

@Entity
@Comment("좌석")
class Seat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Comment("좌석번호")
    val no: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_seat_id", nullable = false)
    val scheduleSeat: ScheduleSeat,
    @OneToMany(mappedBy = "seat", cascade = [CascadeType.ALL])
    val seatReservations: List<SeatReservation> = mutableListOf(),
)
