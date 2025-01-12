package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.user.User
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface UserJpaRepository : JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    fun findByIdWithLock(id: Long): User?
}
