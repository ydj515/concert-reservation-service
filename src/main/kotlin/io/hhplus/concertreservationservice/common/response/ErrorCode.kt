package io.hhplus.concertreservationservice.common.response

enum class ErrorCode(
    val code: String,
    val message: String,
) {
    FAIL("FAIL_01", "Fail"),

    INTERNAL_SERVER_ERROR("Internal server error", "Internal server error"),
}
