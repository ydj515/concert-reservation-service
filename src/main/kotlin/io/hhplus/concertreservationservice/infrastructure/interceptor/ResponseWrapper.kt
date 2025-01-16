package io.hhplus.concertreservationservice.infrastructure.interceptor

import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import java.io.CharArrayWriter
import java.io.PrintWriter

class ResponseWrapper(response: HttpServletResponse) : HttpServletResponseWrapper(response) {
    private val charArrayWriter = CharArrayWriter()
    private val printWriter = PrintWriter(charArrayWriter)

    override fun getWriter(): PrintWriter {
        return printWriter
    }

    fun getResponseBody(): String {
        return charArrayWriter.toString()
    }
}
