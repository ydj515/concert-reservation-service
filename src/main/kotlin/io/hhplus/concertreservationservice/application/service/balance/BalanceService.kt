package io.hhplus.concertreservationservice.application.service.balance

import io.hhplus.concertreservationservice.application.service.balance.request.ChargeBalanceCommand
import io.hhplus.concertreservationservice.application.service.balance.request.FetchBalanceCommand
import io.hhplus.concertreservationservice.application.service.balance.response.ChargedBalanceInfo
import io.hhplus.concertreservationservice.application.service.balance.response.FetchedBalanceInfo
import io.hhplus.concertreservationservice.domain.user.exception.UserNotFoundException
import io.hhplus.concertreservationservice.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BalanceService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun chargeBalance(command: ChargeBalanceCommand): ChargedBalanceInfo {
        val user = userRepository.getUserByIdWithLock(command.userId) ?: throw UserNotFoundException(command.userId)

        user.chargeMoney(command.money)
        val savedUser = userRepository.save(user)
        return ChargedBalanceInfo(
            savedUser.id,
            savedUser.name,
            savedUser.balance,
        )
    }

    fun getBalance(command: FetchBalanceCommand): FetchedBalanceInfo {
        val user = userRepository.getUser(command.userId) ?: throw UserNotFoundException(command.userId)

        return FetchedBalanceInfo(
            user.id,
            user.name,
            user.balance,
        )
    }
}
