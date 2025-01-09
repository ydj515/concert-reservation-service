package io.hhplus.concertreservationservice.domain.user.repository

import io.hhplus.concertreservationservice.domain.user.User
import java.util.Optional

interface UserRepository {
    fun getUserById(id: Long): Optional<User>

    fun getUserByIdWithLock(id: Long): Optional<User>

    fun save(user: User): User

    fun saveAll(users: List<User>): List<User>
}
