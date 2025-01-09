package io.hhplus.concertreservationservice.presentation.advice

import io.hhplus.concertreservationservice.common.response.ErrorCode
import io.hhplus.concertreservationservice.common.response.ErrorResponse
import io.hhplus.concertreservationservice.domain.concert.exception.ConcertNotFoundException
import io.hhplus.concertreservationservice.domain.concert.exception.InvalidReservationStateException
import io.hhplus.concertreservationservice.domain.concert.exception.ReservationNotFoundException
import io.hhplus.concertreservationservice.domain.concert.exception.ScheduleNotFoundException
import io.hhplus.concertreservationservice.domain.concert.exception.SeatNotFoundException
import io.hhplus.concertreservationservice.domain.reservation.exception.AlreadyReservedException
import io.hhplus.concertreservationservice.domain.token.exception.InvalidTokenException
import io.hhplus.concertreservationservice.domain.token.exception.TokenNotFoundException
import io.hhplus.concertreservationservice.domain.user.exception.InsufficientBalanceException
import io.hhplus.concertreservationservice.domain.user.exception.UserNotFoundException
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.format.DateTimeParseException

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

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(TokenNotFoundException::class)
    fun handleTokenNotFoundException(ex: TokenNotFoundException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(AlreadyReservedException::class)
    fun handleAlreadyReservedException(ex: AlreadyReservedException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(InvalidReservationStateException::class)
    fun handleAllInvalidReservationStateException(ex: InvalidReservationStateException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.INTERNAL_SERVER_ERROR)
        return ResponseEntity.internalServerError().body(errorResponse)
    }

    @ExceptionHandler(ReservationNotFoundException::class)
    fun handleAllReservationNotFoundException(ex: ReservationNotFoundException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.NOT_FOUND)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(ScheduleNotFoundException::class)
    fun handleAllScheduleNotFoundException(ex: ScheduleNotFoundException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.NOT_FOUND)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(SeatNotFoundException::class)
    fun handleSeatNotFoundException(ex: SeatNotFoundException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.NOT_FOUND)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(ConcertNotFoundException::class)
    fun handleConcertNotFoundException(ex: ConcertNotFoundException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.NOT_FOUND)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateTimeParseException(ex: DateTimeParseException): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(InsufficientBalanceException::class)
    fun handleInsufficientBalanceException(ex: InsufficientBalanceException): ResponseEntity<ErrorResponse<String>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.BAD_REQUEST)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: Exception): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.UNAUTHORIZED)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<ErrorResponse<String?>> {
        val errorResponse = ErrorResponse.of(ex.message, ErrorCode.INTERNAL_SERVER_ERROR)
        return ResponseEntity.internalServerError().body(errorResponse)
    }
}
