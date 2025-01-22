package io.hhplus.concertreservationservice.presentation.advice

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.concertreservationservice.common.response.ApiResponse
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.lang.reflect.ParameterizedType

@RestControllerAdvice
class GlobalResponseAdvice(private val objectMapper: ObjectMapper) : ResponseBodyAdvice<Any> {
    // controller의 매핑 반환 형식
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>,
    ): Boolean {
        // Swagger 경로 제외
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val path = request.requestURI

        // Swagger 경로가 아닌 경우만 처리
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return false
        }

        var type: Class<*> = returnType.parameterType
        if (ResponseEntity::class.java.isAssignableFrom(type)) {
            try {
                val parameterizedType = returnType.genericParameterType as ParameterizedType
                type = parameterizedType.actualTypeArguments[0] as Class<*>
            } catch (ex: ClassCastException) {
                return false
            } catch (ex: ArrayIndexOutOfBoundsException) {
                return false
            }
        }
        return !ApiResponse::class.java.isAssignableFrom(type)
    }

    // body는 실제로 직렬화할 객체
    override fun beforeBodyWrite(
        @Nullable body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? {
        val httpServletResponse =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response
                ?: throw IllegalStateException("HttpServletResponse is not available")

        val httpStatus = HttpStatus.valueOf(httpServletResponse.status)

        return when {
            httpStatus.isError -> body
            else -> ApiResponse.success(body) // 그 외 응답을 SuccessResponse 로 변환
        }
    }
}
