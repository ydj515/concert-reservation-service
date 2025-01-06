package io.hhplus.concertreservationservice.domain.concert

import io.hhplus.concertreservationservice.infrastructure.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Comment

@Entity
@Comment("콘서트")
class Concert(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Column(nullable = false)
    @Comment("콘서트 제목")
    val title: String,
    @OneToMany(mappedBy = "concert", cascade = [CascadeType.ALL])
    val schedules: List<Schedule> = mutableListOf(),
) : BaseEntity()
