package io.hhplus.concertreservationservice.infrastructure.config.kafka

import io.hhplus.concertreservationservice.domain.payment.event.ExternalPayEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer

@EnableKafka
@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}") private val kafkaHost: String,
    @Value("\${spring.kafka.consumer.auto-offset-reset}") private val autoOffsetReset: String,
    @Value("\${spring.kafka.consumer.group-id-payment}") private val paymentGroupId: String,
) {
    @Bean
    fun couponIssuedEventConsumerFactory(): ConsumerFactory<String, ExternalPayEvent> {
        val config =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaHost,
                ConsumerConfig.GROUP_ID_CONFIG to paymentGroupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to autoOffsetReset,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JsonDeserializer::class.java.name,
                JsonDeserializer.TRUSTED_PACKAGES to "*",
                JsonDeserializer.VALUE_DEFAULT_TYPE to ExternalPayEvent::class.java.name,
            )
        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun kafkaListenerContainerFactoryCoupon(): ConcurrentKafkaListenerContainerFactory<String, ExternalPayEvent> {
        return ConcurrentKafkaListenerContainerFactory<String, ExternalPayEvent>().apply {
            consumerFactory = couponIssuedEventConsumerFactory()
        }
    }
}
