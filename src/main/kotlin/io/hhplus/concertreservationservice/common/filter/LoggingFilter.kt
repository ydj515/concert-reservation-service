package io.hhplus.concertreservationservice.common.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class LoggingFilter(
    private val filterLogger: FilterLogger,
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val responseWrapper = ContentCachingResponseWrapper(response)
        val startTime = System.currentTimeMillis()
        MDC.put("requestId", UUID.randomUUID().toString())

        try {
            val requestWrapper = RequestWrapper(request)
            filterLogger.logRequest(requestWrapper)
            filterChain.doFilter(requestWrapper, responseWrapper)
        } finally {
            filterLogger.logResponse(responseWrapper, startTime)
            responseWrapper.copyBodyToResponse()
        }
        MDC.clear()
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludeFilterPath = arrayOf("/h2-console")
        val path = request.requestURI
        return excludeFilterPath.any { path.startsWith(it) }
    }
}
