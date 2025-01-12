package io.hhplus.concertreservationservice.domain.concert

import io.hhplus.concertreservationservice.domain.balance.Money
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Comment

@Entity
@Comment("스케쥴별 좌석정보")
class ScheduleSeat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Enumerated(EnumType.STRING)
    @Comment("좌석 유형")
    val type: SeatType,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "amount", column = Column(name = "price")),
    )
    var price: Money = Money(0),
    @Comment("좌석 유형별 수")
    val seatCount: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    val schedule: Schedule,
    @OneToMany(mappedBy = "scheduleSeat", fetch = FetchType.LAZY)
    val seats: List<Seat> = mutableListOf(),
)
