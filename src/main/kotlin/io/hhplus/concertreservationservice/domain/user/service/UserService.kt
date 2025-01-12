package io.hhplus.concertreservationservice.domain.user.service

import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.domain.user.exception.InsufficientBalanceException
import io.hhplus.concertreservationservice.domain.user.exception.UserNotFoundException
import io.hhplus.concertreservationservice.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun updateUserBalance(
        user: User,
        money: Money,
    ) {
        if (user.balance.amount < money.amount) {
            throw InsufficientBalanceException(user.id)
        }
        user.balance = user.balance.subtract(money)
        userRepository.save(user)
    }

    fun getUser(id: Long): User {
        return userRepository.getUser(id) ?: throw UserNotFoundException(id)
    }
}
