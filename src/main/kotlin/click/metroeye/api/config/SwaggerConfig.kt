package click.metroeye.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    companion object {
        const val SECURITY_SCHEME_NAME = "Bearer Authentication"
        const val SECURITY_SCHEME_TYPE = "bearer"
        const val SECURITY_SCHEME_FORMAT = "JWT"
    }

    @Value("\${springdoc.swagger-ui.version}")
    private lateinit var API_VERSION: String

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(
                SecurityRequirement().addList(SECURITY_SCHEME_NAME)
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
            )
            .info(
                Info()
                    .title("Metroeye API")
                    .version(API_VERSION)
                    .description("Metroeye API Documentation")
            )
    }
}