package io.hhplus.concertreservationservice.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import
import java.time.Clock
import java.time.ZoneOffset

@Import(ClockConfig::class)
class ClockConfigTest(
    private val clock: Clock,
) : FunSpec({
        test("clock 설정을 테스트 한다.") {
            val currentInstant = clock.instant()
            val zonedDateTime = currentInstant.atZone(ZoneOffset.UTC)

            zonedDateTime.zone.id shouldBe "Z" // 테스트 확인
        }
    })
