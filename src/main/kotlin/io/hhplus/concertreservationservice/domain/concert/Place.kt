package io.hhplus.concertreservationservice.domain.concert

import io.hhplus.concertreservationservice.infrastructure.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.Comment

@Entity
@Comment("공연장")
class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Column(nullable = false, unique = true)
    @Comment("공연장소 명")
    val name: String,
    @Comment("수용좌석수")
    val availableSeatCount: Int,
) : BaseEntity()
