package io.hhplus.concertreservationservice.domain.token.service

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.startWith
import java.util.Date

class TokenProviderTest : FunSpec({

    val secretKey =
        "hihellohihellohihellohihellohihellohihellohihellohihellohihellohihello" +
            "hihellohihellohihellohihellohihellohihellohihellohihellohihellohihello" +
            "hihellohihellohihellohihellohihellohihello"
    val accessTokenExpiration = 1000L * 60 * 60 // 1 시간
    val tokenProvider = TokenProvider(secretKey, accessTokenExpiration)

    context("토큰 생성 및 검증") {
        val userId = 123L

        test("토큰이 성공적으로 생성되어야 한다") {
            val token = tokenProvider.generateToken(userId)

            token shouldNotBe null
            token should startWith("eyJhbGciOiJIUzUxMiJ9") // header
        }

        test("생성된 토큰은 성공적으로 검증되어야 한다") {
            val token = tokenProvider.generateToken(userId)
            val isValid = tokenProvider.validateToken(token)

            isValid shouldBe true
        }

        test("잘못된 토큰은 JwtException throw") {
            val invalidToken = "invalid.token.string"

            shouldThrow<JwtException> {
                tokenProvider.validateToken(invalidToken)
            }
        }

        test("만료된 토큰은 JwtException throw") {
            val keyBytes = Decoders.BASE64.decode(secretKey)
            val key = Keys.hmacShaKeyFor(keyBytes)
            val expiredToken =
                Jwts.builder()
                    .setSubject(userId.toString())
                    .setIssuedAt(Date(System.currentTimeMillis() - 3600 * 1000)) // 1 시간전
                    .setExpiration(Date(System.currentTimeMillis() - 1800 * 1000))
                    .signWith(key)
                    .compact()

            shouldThrow<JwtException> {
                tokenProvider.validateToken(expiredToken)
            }
        }

        test("지원되지 않는 토큰 형식은 JwtException throw") {
            val unsupportedToken = "unsupported.token.format"

            shouldThrow<JwtException> {
                tokenProvider.validateToken(unsupportedToken)
            }
        }
    }
})
