package io.hhplus.concertreservationservice.config

import io.hhplus.concertreservationservice.common.filter.JwtAuthenticationFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {
    @Bean
    fun jwtAuthenticationFilterRegistration(
        jwtAuthenticationFilter: JwtAuthenticationFilter,
    ): FilterRegistrationBean<JwtAuthenticationFilter> {
        val registrationBean = FilterRegistrationBean(jwtAuthenticationFilter)
        registrationBean.addUrlPatterns("/api/*")
        registrationBean.addUrlPatterns("/reservation-token/status")
        registrationBean.addInitParameter("excludePaths", "/reservation-token")
        return registrationBean
    }
}
