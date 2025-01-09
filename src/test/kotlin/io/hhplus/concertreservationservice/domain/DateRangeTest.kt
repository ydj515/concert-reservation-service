package io.hhplus.concertreservationservice.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class DateRangeTest : StringSpec({
    "DateRange 객체를 유효한 날짜 범위로 생성할 수 있다" {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 12, 31)

        val dateRange = DateRange(start = startDate, end = endDate)

        dateRange.start shouldBe startDate
        dateRange.end shouldBe endDate
    }

    "종료일이 시작일보다 앞서면 예외가 발생한다" {
        val startDate = LocalDate.of(2025, 12, 31)
        val endDate = LocalDate.of(2025, 1, 1)

        val result =
            shouldThrow<IllegalArgumentException> {
                DateRange(start = startDate, end = endDate)
            }
        result.message shouldBe "종료일은 시작일 보다 반드시 같거나 뒤에 있어야합니다."
    }

    "종료일이 시작일과 같을 경우 유효한 DateRange 객체를 생성할 수 있다" {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 1, 1)

        val dateRange = DateRange(start = startDate, end = endDate)

        dateRange.start shouldBe startDate
        dateRange.end shouldBe endDate
    }
})
