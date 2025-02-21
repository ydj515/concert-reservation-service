package io.hhplus.concertreservationservice.config

import io.hhplus.concertreservationservice.application.client.ExternalPayClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackageClasses = [ExternalPayClient::class])
class FeignConfig
