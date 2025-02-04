package io.hhplus.concertreservationservice.common.interceptor

import jakarta.servlet.http.HttpServletRequest

data class RequestLog(
    val url: String,
    val method: String,
    val headers: String,
    val queryParams: String,
) {
    override fun toString(): String {
        return "RequestLog(url='$url', method='$method', headers='$headers', queryParams='$queryParams')"
    }
}

fun createRequestLog(request: HttpServletRequest): RequestLog {
    val headers = request.headerNames.toList().joinToString(", ")
    val queryParams = request.parameterMap.entries.joinToString(", ") { "${it.key}=${it.value.joinToString(",")}" }
    return RequestLog(
        url = request.requestURL.toString(),
        method = request.method,
        headers = headers,
        queryParams = queryParams,
    )
}
