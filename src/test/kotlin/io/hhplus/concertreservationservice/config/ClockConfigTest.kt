package io.hhplus.concertreservationservice.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import
import java.time.Clock
import java.time.ZoneId

@Import(ClockConfig::class)
class ClockConfigTest(
    private val clock: Clock,
) : FunSpec({
        test("clock 설정을 테스트 한다.") {
            val currentInstant = clock.instant()
            val zonedDateTime = currentInstant.atZone(ZoneId.of("Asia/Seoul"))

            zonedDateTime.zone.id shouldBe "Asia/Seoul" // 테스트 확인
        }
    })
