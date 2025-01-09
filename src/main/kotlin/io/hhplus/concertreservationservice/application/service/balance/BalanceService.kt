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
        val user =
            userRepository.getUserByIdWithLock(command.userId).orElseThrow {
                UserNotFoundException(command.userId)
            }
        user.balance = user.balance.add(command.amount)
        val savedUser = userRepository.save(user)
        return ChargedBalanceInfo(
            savedUser.id,
            savedUser.name,
            savedUser.balance,
        )
    }

    fun getBalance(command: FetchBalanceCommand): FetchedBalanceInfo {
        val user =
            userRepository.getUserById(command.userId).orElseThrow {
                UserNotFoundException(command.userId)
            }
        return FetchedBalanceInfo(
            user.id,
            user.name,
            user.balance,
        )
    }
}
