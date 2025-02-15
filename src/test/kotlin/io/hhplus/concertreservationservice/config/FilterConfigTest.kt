package io.hhplus.concertreservationservice.config

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("e2e-test")
class FilterConfigTest(
    private val applicationContext: ApplicationContext,
) : StringSpec({

        "필터가 잘 등록되었는지 확인" {
            val filterBean = applicationContext.getBean(FilterRegistrationBean::class.java)

            val urlPatterns = filterBean.urlPatterns
            urlPatterns shouldContain "/api/*"
            urlPatterns shouldContain "/reservation-token/status"

            // 필터 제외 경로 확인
            val excludePaths = filterBean.initParameters["excludePaths"]
            excludePaths shouldBe "/reservation-token, /api/pay/send"
        }
    })
