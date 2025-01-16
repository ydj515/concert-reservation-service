package io.hhplus.concertreservationservice.common.response

import org.springframework.http.HttpStatus

object ApiResponse {
    private val DEFAULT_SUCCESS_RESULT_CODE = SuccessCode.SUCCESS
    private val DEFAULT_FAIL_RESULT_CODE = ErrorCode.FAIL

    fun <T> success(data: T): SuccessResponse<T> {
        return success(data, DEFAULT_SUCCESS_RESULT_CODE)
    }

    fun <T> success(
        data: T,
        resultCode: SuccessCode,
    ): SuccessResponse<T> {
        return SuccessResponse(
            code = resultCode.code,
            message = resultCode.message,
            data = data,
        )
    }

    fun <T> created(data: T): SuccessResponse<T> {
        return created(data, DEFAULT_SUCCESS_RESULT_CODE)
    }

    fun <T> created(
        data: T,
        resultCode: SuccessCode,
    ): SuccessResponse<T> {
        return SuccessResponse(
            code = resultCode.code,
            message = resultCode.message,
            data = data,
        )
    }

    fun <T> failed(data: T): ErrorResponse<T> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun <T> failedNotFound(data: T): ErrorResponse<T> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.NOT_FOUND)
    }

    fun <T> failedNotFound(
        data: T,
        resultCode: ErrorCode,
    ): ErrorResponse<T> {
        return failed(data, resultCode, HttpStatus.NOT_FOUND)
    }

    fun <T> failedBadRequest(data: T): ErrorResponse<T> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.BAD_REQUEST)
    }

    fun <T> failedForbidden(data: T): ErrorResponse<T> {
        return failed(data, DEFAULT_FAIL_RESULT_CODE, HttpStatus.FORBIDDEN)
    }

    fun <T> failed(
        data: T,
        resultCode: ErrorCode,
        httpStatus: HttpStatus,
    ): ErrorResponse<T> {
        return ErrorResponse(
            code = resultCode.code,
            message = resultCode.message,
            data = data,
        )
    }
}
