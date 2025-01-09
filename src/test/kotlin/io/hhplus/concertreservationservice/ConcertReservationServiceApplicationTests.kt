package io.hhplus.concertreservationservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.utility.TestcontainersConfiguration

@Import(TestcontainersConfiguration::class)
@ActiveProfiles("integration-test")
@SpringBootTest
@Transactional
class ConcertReservationServiceApplicationTests {
    @Test
    fun contextLoads() {
    }
}
