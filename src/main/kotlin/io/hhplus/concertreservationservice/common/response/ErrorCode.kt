package io.hhplus.concertreservationservice.common.response

enum class ErrorCode(
    val code: String,
    val message: String,
) {
    FAIL("FAIL_01", "Fail"),
    BAD_REQUEST("FAIL_02", "Bad Request"),
    UNAUTHORIZED("FAIL_03", "Unauthorized"),
    NOT_FOUND("FAIL_04", "not found"),

    TOKEN_ERROR("FAIL_01", "Request is invalid"),

    INTERNAL_SERVER_ERROR("Internal server error", "Internal server error"),
}
