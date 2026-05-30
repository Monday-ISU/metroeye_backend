package click.metroeye.api.config

import click.metroeye.api.presentation.v1.controller.AuthController
import click.metroeye.api.presentation.v1.controller.DeviceController
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val SECURITY_SCHEME_NAME = "Bearer Authentication"
        const val SECURITY_SCHEME_TYPE = "bearer"
        const val SECURITY_SCHEME_FORMAT = "JWT"

        const val CLIENT_VERSION_SCHEME_NAME = "Client-Version"
    }

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(
                SecurityRequirement()
                    .addList(SECURITY_SCHEME_NAME)
                    .addList(CLIENT_VERSION_SCHEME_NAME)
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .bearerFormat(SECURITY_SCHEME_FORMAT)
                            .scheme(SECURITY_SCHEME_TYPE)
                            .description("JWT 토큰을 Bearer 형식으로 전달합니다.")
                    )
                    .addSecuritySchemes(
                        CLIENT_VERSION_SCHEME_NAME,
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name(CLIENT_VERSION_SCHEME_NAME)
                            .description("클라이언트 버전")
                    )
            )
            .info(
                Info()
                    .title("Metroeye API")
                    .version("1.0.0")
                    .description("Metroeye API Documentation")
            )
    }

    @Bean
    fun operationCustomizer(): OperationCustomizer {
        return OperationCustomizer { operation, handlerMethod ->
            if (handlerMethod.beanType in listOf(DeviceController::class.java, AuthController::class.java)) {
                return@OperationCustomizer operation
            }

            operation.responses.addApiResponse(
                "401",
                ApiResponse().content(
                    Content().addMediaType(
                        "application/json",
                        MediaType()
                            .addExamples(
                                "인증 헤더 누락",
                                Example()
                                    .summary("인증(Authorization) 헤더에 액세스 토큰(accessToken)을 포함하지 않은 경우 발생합니다.")
                                    .value(objectMapper.readTree(
                                        """
                                        {
                                            "clientMessage": "인증 정보가 필요합니다.",
                                            "serverMessage": "Authorization header is missing.",
                                            "data": null
                                        }
                                    """))
                            )
                            .addExamples(
                                "엑세스 토큰 만료",
                                Example()
                                    .summary("만료된 엑세스 토큰(accessToken)으로 요청할 경우 발생합니다.")
                                    .value(objectMapper.readTree("""
                                        {
                                            "clientMessage": "인증에 실패했습니다.",
                                            "serverMessage": "Access token has expired.",
                                            "data": null
                                        }
                                    """))
                            )
                            .addExamples(
                                "엑세스 토큰 형식 오류",
                                Example()
                                    .summary("엑세스 토큰(accessToken) 형식이 유효하지 않을 경우 발생합니다.")
                                    .value(objectMapper.readTree("""
                                        {
                                            "clientMessage": "인증에 실패했습니다.",
                                            "serverMessage": "Access token is invalid.",
                                            "data": null
                                        }
                                    """))
                            )
                            .addExamples(
                                "엑세스 토큰 UUID 누락",
                                Example()
                                    .summary("엑세스 토큰(accessToken)에서 기기 고유 번호(uuid)를 확인할 수 없는 경우 발생합니다.")
                                    .value(objectMapper.readTree("""
                                        {
                                            "clientMessage": "인증에 실패했습니다.",
                                            "serverMessage": "Access token subject is missing.",
                                            "data": null
                                        }
                                    """))
                            )
                            .addExamples(
                                "엑세스 토큰 타입 누락",
                                Example()
                                    .summary("엑세스 토큰(accessToken)에서 타입(type)를 확인할 수 없는 경우 발생합니다.")
                                    .value(objectMapper.readTree("""
                                        {
                                            "clientMessage": "인증에 실패했습니다.",
                                            "serverMessage": "Token type is missing.",
                                            "data": null
                                        }
                                    """))
                            )
                            .addExamples(
                                "엑세스 토큰 타입 불일치",
                                Example()
                                    .summary("액세스 토큰(accessToken)이 아닌 다른 타입의 토큰으로 인증 요청한 경우 발생합니다.")
                                    .value(objectMapper.readTree("""
                                        {
                                            "clientMessage": "인증에 실패했습니다.",
                                            "serverMessage": "Token is not an access token.",
                                            "data": null
                                        }
                                    """))
                            )
                    )
                )
            )
            operation
        }
    }
}