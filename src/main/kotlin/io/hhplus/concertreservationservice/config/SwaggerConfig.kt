package io.hhplus.concertreservationservice.config

import io.hhplus.concertreservationservice.presentation.constants.HeaderConstants.RESERVATION_QUEUE_TOKEN
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("concert reservation service")
                    .version("1.0.0")
                    .description("concert reservation service 입니다."),
            )
            .addSecurityItem(
                SecurityRequirement().addList(RESERVATION_QUEUE_TOKEN),
            )
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        RESERVATION_QUEUE_TOKEN,
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name(RESERVATION_QUEUE_TOKEN)
                            .description("콘서트 예약 서비스 이용을 위한 대기열 순서 토큰"),
                    ),
            )
    }

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/**")
            .build()
    }
}
