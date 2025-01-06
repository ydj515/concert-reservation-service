package io.hhplus.concertreservationservice.common.response

import java.time.LocalDateTime

data class SuccessResponse<T>(
    val success: Boolean = true,
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: T?,
) {
    companion object {
        fun <T> of(
            data: T,
            successCode: SuccessCode,
        ): SuccessResponse<T> {
            return SuccessResponse(
                code = successCode.code,
                message = successCode.message,
                data = data,
            )
        }
    }
}
