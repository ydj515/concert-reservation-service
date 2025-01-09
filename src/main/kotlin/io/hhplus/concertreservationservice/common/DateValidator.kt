package io.hhplus.concertreservationservice.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateValidator {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun validateAndParse(date: String): LocalDate {
        return try {
            LocalDate.parse(date, formatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd.")
        }
    }
}
