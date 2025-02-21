package io.hhplus.concertreservationservice

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class TestKafkaListener {
    val receivedMessages: MutableList<String> = Collections.synchronizedList(mutableListOf())

    @KafkaListener(topics = ["sample-topic"], groupId = "test-group")
    fun listen(message: String) {
        receivedMessages.add(message)
    }
}
