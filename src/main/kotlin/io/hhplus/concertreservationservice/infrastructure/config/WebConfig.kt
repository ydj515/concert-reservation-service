package io.hhplus.concertreservationservice.infrastructure.config

import io.hhplus.concertreservationservice.infrastructure.interceptor.RequestResponseLoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RequestResponseLoggingInterceptor())
            .addPathPatterns("/**")
    }
}
