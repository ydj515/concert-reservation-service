package io.hhplus.concertreservationservice.config

import io.hhplus.concertreservationservice.application.job.PaymentRepublishJob
import io.hhplus.concertreservationservice.application.job.ReservationExpireJob
import io.hhplus.concertreservationservice.application.job.TokenActivationJob
import io.hhplus.concertreservationservice.application.job.TokenDeactivationJob
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@Profile("!test && !integration-test && !e2e-test")
@EnableTransactionManagement
class QuartzConfig {
    // TokenActivationJob 설정
    @Bean
    fun tokenActivationJobDetail(): JobDetail {
        return JobBuilder.newJob(TokenActivationJob::class.java)
            .withIdentity("TokenActivationJob", "TokenGroup")
            .withDescription("Activate tokens periodically")
            .storeDurably(true)
            .build()
    }

    @Bean
    fun tokenActivationTrigger(): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(tokenActivationJobDetail())
            .withIdentity("TokenActivationTrigger", "TokenGroup")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(10) // 10초마다 실행
                    .repeatForever(),
            )
            .build()
    }

    // TokenDeactivationJob 설정
    @Bean
    fun tokenDeactivationJobDetail(): JobDetail {
        return JobBuilder.newJob(TokenDeactivationJob::class.java)
            .withIdentity("TokenDeactivationJob", "TokenGroup")
            .withDescription("Deactivate tokens periodically")
            .storeDurably(true)
            .build()
    }

    @Bean
    fun tokenDeactivationTrigger(): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(tokenDeactivationJobDetail())
            .withIdentity("TokenDeactivationTrigger", "TokenGroup")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(15) // 15초마다 실행
                    .repeatForever(),
            )
            .build()
    }

    // ReservationExpireJob 설정
    @Bean
    fun reservationExpireJobDetail(): JobDetail {
        return JobBuilder.newJob(ReservationExpireJob::class.java)
            .withIdentity("ReservationExpireJob", "ReservationGroup")
            .withDescription("expire reservation periodically")
            .storeDurably(true)
            .build()
    }

    @Bean
    fun reservationExpireTrigger(): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(reservationExpireJobDetail())
            .withIdentity("ReservationExpireTrigger", "ReservationGroup")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(5) // 5초마다 실행
                    .repeatForever(),
            )
            .build()
    }

    // PaymentRepublishJob 설정
    @Bean
    fun paymentRepublishJobDetail(): JobDetail {
        return JobBuilder.newJob(PaymentRepublishJob::class.java)
            .withIdentity("PaymentRepublishJob", "PaymentRepublishGroup")
            .withDescription("republish PaymentOutbox periodically")
            .storeDurably(true)
            .build()
    }

    @Bean
    fun paymentRepublishTrigger(): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(paymentRepublishJobDetail())
            .withIdentity("PaymentRepublishTrigger", "PaymentRepublishGroup")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(5) // 5분마다 실행
                    .repeatForever(),
            )
            .build()
    }
}
