package io.hhplus.concertreservationservice.domain.payment

import io.hhplus.concertreservationservice.domain.Money
import io.hhplus.concertreservationservice.domain.user.User
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
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

@Entity
@Comment("결제")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "amount", column = Column(name = "totalCost")),
    )
    var totalCost: Money = Money(0),
    @Comment("결제상태")
    @Enumerated(EnumType.STRING)
    val status: PaymentStatus,
    @Comment("결제시간")
    val paidAt: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Comment("사용자")
    val user: User,
)
