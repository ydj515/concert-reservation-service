package io.hhplus.concertreservationservice.infrastructure.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RequestResponseLoggingInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val requestLog = createRequestLog(request)
        logger.info("Request: $requestLog")
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: org.springframework.web.servlet.ModelAndView?,
    ) {
        logger.info("Response: ${response.status}")
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val responseWrapper = response as? ResponseWrapper
        responseWrapper?.let {
            val responseBody = it.getResponseBody()
            logger.info("Response : $responseBody")
        }

        ex?.let {
            logger.error("Exception occurred while processing the request: ${it.message}", it)
        }
    }
}
