package io.hhplus.concertreservationservice.infrastructure.persistence.jpa

import io.hhplus.concertreservationservice.domain.concert.Place
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceJpaRepository : JpaRepository<Place, Long>
