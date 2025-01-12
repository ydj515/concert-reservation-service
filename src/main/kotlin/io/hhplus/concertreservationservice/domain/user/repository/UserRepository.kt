package io.hhplus.concertreservationservice.domain.user.repository

import io.hhplus.concertreservationservice.domain.user.User

interface UserRepository {
    fun getUser(id: Long): User?

    fun getUserByIdWithLock(id: Long): User?

    fun save(user: User): User

    fun saveAll(users: List<User>): List<User>
}
