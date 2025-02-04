package io.hhplus.concertreservationservice.config

import io.hhplus.concertreservationservice.common.interceptor.RequestResponseLoggingInterceptor
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
