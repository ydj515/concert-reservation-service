package io.hhplus.concertreservationservice.presentation.advice

import io.hhplus.concertreservationservice.common.response.ErrorCode
import io.hhplus.concertreservationservice.common.response.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors =
            ex.bindingResult.allErrors.associate {
                (it as FieldError).field to it.defaultMessage
            }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleValidationExceptions(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.FAIL)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.INTERNAL_SERVER_ERROR)
        return ResponseEntity.internalServerError().body(errorResponse)
    }
}
