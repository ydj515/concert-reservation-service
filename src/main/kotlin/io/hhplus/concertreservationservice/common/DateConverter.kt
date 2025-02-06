package io.hhplus.concertreservationservice.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun localDateTimeToDouble(localDateTime: LocalDateTime): Double {
    val instant = localDateTime.toInstant(ZoneOffset.UTC)
    return instant.toEpochMilli().toDouble()
}

fun localDateTimeToLong(localDateTime: LocalDateTime): Long {
    val instant = localDateTime.toInstant(ZoneOffset.UTC)
    return instant.toEpochMilli()
}

fun longToLocalDateTime(epochMilli: Long): LocalDateTime {
    val instant = Instant.ofEpochMilli(epochMilli)
    return instant.atZone(ZoneOffset.UTC).toLocalDateTime()
}

fun doubleToLocalDateTime(epochMilli: Double): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli.toLong()), ZoneOffset.UTC)
}
