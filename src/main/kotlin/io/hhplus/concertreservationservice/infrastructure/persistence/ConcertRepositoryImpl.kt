package io.hhplus.concertreservationservice.infrastructure.persistence

import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.repository.ConcertRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ConcertJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ConcertRepositoryImpl(private val concertJpaRepository: ConcertJpaRepository) : ConcertRepository {
    override fun getConcert(id: Long): Optional<Concert> {
        return concertJpaRepository.findById(id)
    }

    override fun getConcerts(pageable: Pageable): Page<Concert> {
        return concertJpaRepository.findAll(pageable)
    }

    override fun save(concert: Concert): Concert {
        return concertJpaRepository.save(concert)
    }
}
