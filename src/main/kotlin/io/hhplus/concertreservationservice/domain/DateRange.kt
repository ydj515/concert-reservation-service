package io.hhplus.concertreservationservice.domain

import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
class DateRange(
    val start: LocalDate,
    val end: LocalDate,
) {
    init {
        require(!end.isBefore(start)) { "종료일은 시작일 보다 반드시 같거나 뒤에 있어야합니다." }
    }
}
