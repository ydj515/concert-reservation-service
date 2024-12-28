package io.hhplus.concertreservationservice

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<ConcertReservationServiceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
