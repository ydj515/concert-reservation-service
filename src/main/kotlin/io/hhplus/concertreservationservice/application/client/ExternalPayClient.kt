package io.hhplus.concertreservationservice.application.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.PropertySource
import org.springframework.web.bind.annotation.PostMapping

@PropertySource("classpath:feign.yml")
@FeignClient(name = "external-pay-service", url = "http://localhost:18080/api/pay")
interface ExternalPayClient {
    @PostMapping("/send")
    fun sendPayResult(externalPayCommand: ExternalPayCommand): ExternalPayResult
}

data class ExternalPayCommand(
    val userName: String,
    val reservationId: Long,
    val amount: Long,
)

data class ExternalPayResult(
    val status: Boolean,
)
