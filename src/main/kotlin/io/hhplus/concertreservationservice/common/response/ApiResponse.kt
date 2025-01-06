package io.hhplus.concertreservationservice.common.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ApiResponse {
    private val DEFAULT_SUCCESS_RESULT_CODE = SuccessCode.SUCCESS
    private val DEFAULT_FAIL_RESULT_CODE = ErrorCode.FAIL

    fun <T> success(data: T): ResponseEntity<SuccessResponse<T>> {
        return success(data, DEFAULT_SUCCESS_RESULT_CODE)
    }

    fun <T> success(
        data: T,
        resultCode: SuccessCode,
    ): ResponseEntity<SuccessResponse<T>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            SuccessResponse(
                code = resultCode.code,
                message = resultCode.message,
                data = data,
            ),
        )
    }

    fun <T> created(data: T): ResponseEntity<SuccessResponse<T>> {
        return created(data, DEFAULT_SUCCESS_RESULT_CODE)
    }

    fun <T> created(
        data: T,
        resultCode: SuccessCode,
    ): ResponseEntity<SuccessResponse<T>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            SuccessResponse(
                code = resultCode.code,
                message = resultCode.message,
                data = data,
            ),
        )
    }

    fun <T> failed(data: T): ResponseEntity<ErrorResponse<T>> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun <T> failedNotFound(data: T): ResponseEntity<ErrorResponse<T>> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.NOT_FOUND)
    }

    fun <T> failedNotFound(
        data: T,
        resultCode: ErrorCode,
    ): ResponseEntity<ErrorResponse<T>> {
        return failed(data, resultCode, HttpStatus.NOT_FOUND)
    }

    fun <T> failedBadRequest(data: T): ResponseEntity<ErrorResponse<T>> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.BAD_REQUEST)
    }

    fun <T> failedForbidden(data: T): ResponseEntity<ErrorResponse<T>> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.FORBIDDEN)
    }

    fun <T> failed(
        data: T,
        resultCode: ErrorCode,
        httpStatus: HttpStatus,
    ): ResponseEntity<ErrorResponse<T>> {
        return ResponseEntity.status(httpStatus).body(
            ErrorResponse(
                code = resultCode.code,
                message = resultCode.message,
                data = data,
            ),
        )
    }
}
