package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.dto.IssueTokenRequestModel
import click.metroeye.api.application.service.AuthService
import click.metroeye.api.presentation.v1.dto.request.IssueTokenRequest
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.IssueTokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Auth API", description = "인증 API")
class AuthController(
    private val authService: AuthService
) {
    @Operation(
        summary = "토큰 발급 API",
        method = "POST",
        description = """
*인증 유형에 따른 토큰을 발급합니다.*

### [Request Body]

- **grantType**: 인증 유형 (CLIENT_CREDENTIALS, REFRESH_TOKEN)
- **uuid**: 기기 고유 번호
- **secret**: 기기 비밀키
- **refreshToken**: 리프레시 토큰

### [인증 방식 설명]

- **CLIENT_CREDENTIALS**: uuid와 secret을 사용하여 토큰 발급
- **REFRESH_TOKEN**: refreshToken을 사용하여 액세스 토큰 재발급
"""
    )
    @PostMapping("/token")
    fun issueToken(
        @RequestBody issueTokenRequest: IssueTokenRequest
    ): Mono<ResponseEntity<ApiResponse<IssueTokenResponse>>> {
        val issueTokenRequestModel = IssueTokenRequestModel.of(
            issueTokenRequest.grantType,
            issueTokenRequest.uuid,
            issueTokenRequest.secret,
            issueTokenRequest.refreshToken
        )

        return authService.issueToken(issueTokenRequestModel)
            .map { issueTokenResponse ->
                ResponseEntity.ok(
                    ApiResponse(
                        "갱신되었습니다.",
                        "SUCCESS",
                        issueTokenResponse
                    )
                )
            }
    }
}