package io.hhplus.concertreservationservice.domain.token

import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

@Entity
@Table(name = "user_reservation_token")
@Comment("사용자 대기열 토큰")
class ReservationToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Comment("토큰 만료일")
    val expiredAt: LocalDateTime,
    @Enumerated(EnumType.STRING)
    @Comment("토큰 상태")
    val status: TokenStatus = TokenStatus.WAITING,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : BaseEntity()
