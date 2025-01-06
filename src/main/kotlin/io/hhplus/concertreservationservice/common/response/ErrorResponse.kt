package io.hhplus.concertreservationservice.common.response

import java.time.LocalDateTime

// success가 없다면으로 에러를 판단 하기 위해 success 없음
data class ErrorResponse<T>(
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: T?,
) {
    companion object {
        fun <T> of(
            data: T,
            errorCode: ErrorCode,
        ): ErrorResponse<T> {
            return ErrorResponse(
                code = errorCode.code,
                message = errorCode.message,
                data = data,
            )
        }
    }
}
