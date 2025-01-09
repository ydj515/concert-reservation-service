package io.hhplus.concertreservationservice.domain.concert.repository

import io.hhplus.concertreservationservice.domain.concert.Concert
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface ConcertRepository {
    fun getConcert(id: Long): Optional<Concert>

    fun getConcerts(pageable: Pageable): Page<Concert>

    fun save(concert: Concert): Concert
}
