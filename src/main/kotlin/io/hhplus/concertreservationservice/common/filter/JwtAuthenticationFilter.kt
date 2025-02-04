package io.hhplus.concertreservationservice.common.filter

import io.hhplus.concertreservationservice.domain.token.exception.InvalidTokenException
import io.hhplus.concertreservationservice.infrastructure.TokenProvider
import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)

        try {
            if (!tokenProvider.validateToken(token)) {
                throw InvalidTokenException()
            }

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                """
                {
                    "code": "FAIL_03",
                    "message": "Unauthorized",
                    "timestamp": "${LocalDateTime.now()}",
                    "data": "Invalid or missing $RESERVATION_QUEUE_TOKEN header"
                }
                """.trimIndent(),
            )
        }
    }

    private fun resolveToken(request: HttpServletRequest): String {
        return request.getHeader(RESERVATION_QUEUE_TOKEN)
//            ?.takeIf { it.startsWith(BEARER_PREFIX) }
//            ?.substring(BEARER_PREFIX.length)
            ?: throw IllegalArgumentException("Reservation token is missing or invalid")
    }
}
