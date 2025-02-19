package io.hhplus.concertreservationservice.presentation.controller.external

import io.hhplus.concertreservationservice.application.client.ExternalPayCommand
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pay")
class PayController {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/send")
    fun send(
        @RequestBody externalPayCommand: ExternalPayCommand,
    ) {
        logger.info("Sending external pay : {}", externalPayCommand)
    }
}
