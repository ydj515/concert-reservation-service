package io.hhplus.concertreservationservice.domain.payment.outbox

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

@Entity
@Comment("결제완료 outbox")
class PaymentCompletedOutboxEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Column(columnDefinition = "TEXT")
    val payload: String,
    @Column
    val paymentId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Enumerated(EnumType.STRING)
    var status: OutboxStatus = OutboxStatus.PENDING,
) {
    fun complete() {
        this.status = OutboxStatus.PROCESSED
    }
}
