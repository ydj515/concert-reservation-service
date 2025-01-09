package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.Optional

interface ReservationTokenJpaRepository : JpaRepository<ReservationToken, Long> {
    fun findByToken(token: String): Optional<ReservationToken>
    fun findByExpiredAtBefore(currentTime: LocalDateTime): List<ReservationToken>

    @Query(
        """
        SELECT COUNT(rt) 
        FROM ReservationToken rt 
        WHERE rt.status = :status
        """
    )
    fun countByStatus(@Param("status") status: TokenStatus): Int

    @Query(
        """
        SELECT rt 
        FROM ReservationToken rt 
        WHERE rt.status = :status 
        ORDER BY rt.id ASC
        """
    )
    fun findWaitingTokensForActivation(
        @Param("status") status: TokenStatus,
        pageable: Pageable
    ): List<ReservationToken>

    @Modifying
    @Query(
        """
        UPDATE ReservationToken rt 
        SET rt.status = :newStatus 
        WHERE rt.id IN :ids
        """
    )
    @Transactional
    fun updateTokenStatus(@Param("ids") ids: List<Long>, @Param("newStatus") newStatus: TokenStatus): Int

    fun deleteByToken(token: String)
}
