package io.hhplus.concertreservationservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig {
    @Bean
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5 // 최소 스레드 개수
        executor.maxPoolSize = 10 // 최대 스레드 개수
        executor.queueCapacity = 100 // 큐 크기 (대기열)
        executor.setThreadNamePrefix("AsyncExecutor-") // 스레드 이름 설정
        executor.initialize()
        return executor
    }
}
