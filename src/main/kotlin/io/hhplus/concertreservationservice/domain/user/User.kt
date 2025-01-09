package io.hhplus.concertreservationservice.domain.user

import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.infrastructure.persistence.BaseEntity
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.Comment

@Entity
@Comment("사용자")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Column(nullable = false, unique = true)
    @Comment("사용자 이름")
    val name: String,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "amount", column = Column(name = "balance")),
    )
    var balance: Money = Money(0),
) : BaseEntity()
