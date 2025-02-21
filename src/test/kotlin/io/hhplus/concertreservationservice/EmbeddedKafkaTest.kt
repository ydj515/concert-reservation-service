package io.hhplus.concertreservationservice

import io.hhplus.concertreservationservice.common.Topic.PAYMENT_COMPLETED_TOPIC
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import org.awaitility.Awaitility.await
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue

@ActiveProfiles("integration-test")
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = [
        "auto.offset.reset=earliest",
        "listeners=PLAINTEXT://localhost:9092"
    ],
    ports = [9092]
)
class EmbeddedKafkaTest(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val testKafkaListener: TestKafkaListener,
) : BehaviorSpec({

    val sampleTopic = "sample-topic"

    given("Kafka 메시지를 전송하면") {
        `when`("Kafka가 정상적으로 동작하면") {
            val event = "sample-event"
            kafkaTemplate.send(sampleTopic, event)
            kafkaTemplate.flush()

            then("Listener가 메시지를 받아야 한다") {
                await().atMost(Duration.ofSeconds(5))
                    .until { testKafkaListener.receivedMessages.isNotEmpty() }

                testKafkaListener.receivedMessages shouldContain event
            }
        }
    }
})
