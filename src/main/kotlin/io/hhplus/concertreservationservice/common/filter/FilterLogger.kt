package io.hhplus.concertreservationservice.common.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.InputStream
import java.nio.charset.StandardCharsets

@Component
class FilterLogger {
    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()

    fun logRequest(request: HttpServletRequest) {
        val queryString = request.queryString
        val headers = getRequestHeaders(request)
        val body = getBody(request.inputStream)

        log.info(
            ">>> Request : Request ID=[{}], Method=[{}] URI=[{}], Content-Type=[{}], Headers=[{}], Body=[{}]",
            MDC.get("requestId"),
            request.method,
            "${request.requestURI}${queryString?.let { "?$it" } ?: ""}",
            request.contentType,
            headers,
            body,
        )
    }

    fun logResponse(
        response: ContentCachingResponseWrapper,
        startTime: Long,
    ) {
        val responseHeaders = getResponseHeaders(response)
        val body = getBody(response.contentInputStream)

        log.info(
            "<<< Response : Request ID=[{}], Status Code=[{}], Headers=[{}], Body=[{}], Execution Time=[{}ms]",
            MDC.get("requestId"),
            response.status,
            responseHeaders,
            body,
            (System.currentTimeMillis() - startTime),
        )
    }

    private fun getRequestHeaders(request: HttpServletRequest): String {
        val headers =
            request.headerNames?.toList()?.joinToString(", ") {
                val value = request.getHeader(it)
                "$it=$value"
            } ?: "No Headers"
        return headers
    }

    private fun getResponseHeaders(response: ContentCachingResponseWrapper): String {
        val headerNames = response.headerNames
        return headerNames.joinToString(", ") {
            "$it=${response.getHeader(it)}"
        }
    }

    private fun getBody(inputStream: InputStream): String? {
        val content = StreamUtils.copyToByteArray(inputStream)
        if (content.isEmpty()) return null
        val rawBody = String(content, StandardCharsets.UTF_8)

        return try {
            val jsonNode = objectMapper.readTree(rawBody)
            objectMapper.writeValueAsString(jsonNode)
        } catch (e: Exception) {
            // JSON 포맷팅 실패 시 원본 문자열 그대로 반환
            rawBody
        }
    }
}
