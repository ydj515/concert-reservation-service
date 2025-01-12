package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.domain.user.repository.UserRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun getUser(id: Long): User? {
        return userJpaRepository.findByIdOrNull(id)
    }

    override fun getUserByIdWithLock(id: Long): User? {
        return userJpaRepository.findByIdWithLock(id)
    }

    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }

    override fun saveAll(users: List<User>): List<User> {
        return userJpaRepository.saveAll(users)
    }
}
