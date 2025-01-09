package io.hhplus.concertreservationservice.application.service.token

import io.hhplus.concertreservationservice.application.service.token.request.CreateTokenCommand
import io.hhplus.concertreservationservice.application.service.token.request.TokenStatusCommand
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.exception.TokenNotFoundException
import io.hhplus.concertreservationservice.domain.token.repository.ReservationTokenRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TokenServiceTest : BehaviorSpec({

    val reservationTokenRepository = mockk<ReservationTokenRepository>()
    val tokenService = TokenService(reservationTokenRepository)

    given("토큰 생성 요청이 들어왔을 때") {
        val command =
            CreateTokenCommand(
                token = "token123",
                userId = 1L,
            )

        val token =
            ReservationToken(
                token = command.token,
                expiredAt = LocalDateTime.now().plusMinutes(5),
                userId = command.userId,
            )

        every { reservationTokenRepository.saveToken(any()) } returns token

        `when`("토큰을 저장하면") {
            val result = tokenService.saveToken(command)

            then("토큰 정보가 정상적으로 반환되어야 한다") {
                assertEquals(token.token, result.token)
                assertEquals(token.expiredAt, result.expiredAt)
                assertEquals(token.userId, result.userId)
            }

            then("토큰이 저장되어야 한다") {
                verify(exactly = 1) { reservationTokenRepository.saveToken(any()) }
            }
        }
    }

    given("토큰 상태 조회 요청이 있을 때") {
        val command = TokenStatusCommand(token = "token123")
        val token =
            ReservationToken(
                token = command.token,
                expiredAt = LocalDateTime.now().plusMinutes(5),
                userId = 1L,
            )

        every { reservationTokenRepository.getToken(command) } returns Optional.of(token)

        `when`("토큰을 조회하면") {
            val result = tokenService.getToken(command)

            then("토큰 정보가 정상적으로 반환되어야 한다") {
                assertEquals(token.token, result.token)
                assertEquals(token.expiredAt, result.expiredAt)
                assertEquals(token.userId, result.userId)
            }

            then("토큰을 조회한 기록이 있어야 한다") {
                verify(exactly = 1) { reservationTokenRepository.getToken(command) }
            }
        }

        `when`("없는 토큰을 조회하면") {
            every { reservationTokenRepository.getToken(command) } returns Optional.empty()

            then("예외가 발생해야 한다") {
                assertFailsWith<TokenNotFoundException> {
                    tokenService.getToken(command)
                }
            }
        }
    }

    given("토큰 삭제 요청이 있을 때") {
        val token = "token123"

        `when`("토큰을 삭제하면") {
            every { reservationTokenRepository.deleteTokenByName(token) } returns Unit
            tokenService.deleteToken(token)

            then("토큰이 삭제되어야 한다") {
                verify(exactly = 1) { reservationTokenRepository.deleteTokenByName(token) }
            }
        }
    }
})
