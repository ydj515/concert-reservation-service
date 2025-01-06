package io.hhplus.concertreservationservice.domain.concert

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "concert_schedule")
@Comment("콘서트 스케쥴")
class Schedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Comment("공연일정")
    val performanceDate: LocalDate,
    @Comment("공연시간(분단위)")
    val performanceTime: Int,
    @Comment("예약시작 날짜")
    val reservationStartedAt: LocalDateTime,
    @Comment("예약종료 날짜")
    val reservationEndedAt: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    val concert: Concert,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    val place: Place,
    @OneToMany(mappedBy = "schedule", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val scheduleSeats: List<ScheduleSeat> = mutableListOf(),
)
