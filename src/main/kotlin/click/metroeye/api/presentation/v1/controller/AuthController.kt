package click.metroeye.api.presentation.v1.controller

import click.metroeye.api.application.dto.IssueTokenRequestModel
import click.metroeye.api.application.service.AuthService
import click.metroeye.api.presentation.v1.dto.request.IssueTokenRequest
import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import click.metroeye.api.presentation.v1.dto.response.IssueTokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "clientMessage": "갱신되었습니다.",
                                        "serverMessage": "SUCCESS",
                                        "data": {
                                            "accessToken": "json-web-token",
                                            "refreshToken": "json-web-token",
                                            "expiresIn": 1800
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "기기 정보 없음",
                                description = "요청한 기기 고유 번호(uuid)로 등록된 기기를 찾을 수 없는 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증에 실패했습니다.",
                                        "serverMessage": "Device not found.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "[CLIENT_CREDENTIALS] 기기 비밀키 불일치",
                                description = "요청한 기기 비밀키(secret)가 등록된 기기의 비밀키와 일치하지 않는 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "인증에 실패했습니다.",
                                        "serverMessage": "Device secret does not match.",
                                        "data": null
                                    }
                                """
                            ),

                            ExampleObject(
                                name = "[REFRESH_TOKEN] 리프레시 토큰 만료",
                                description = "만료된 리프레시 토큰(refreshToken)으로 엑세스 토큰(accessToken) 재발급 요청을 할 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "토큰이 만료되었습니다.",
                                        "serverMessage": "Refresh token has expired.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "[REFRESH_TOKEN] 리프레시 토큰 형식 오류",
                                description = "리프레시 토큰(refreshToken) 형식이 유효하지 않을 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "유효하지 않은 토큰입니다.",
                                        "serverMessage": "Refresh token is invalid.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "[REFRESH_TOKEN] 리프레시 토큰 UUID 누락",
                                description = "리프레시 토큰(refreshToken)에서 기기 고유 번호(uuid)를 확인할 수 없는 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "유효하지 않은 토큰입니다.",
                                        "serverMessage": "Refresh token subject is missing.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "[REFRESH_TOKEN] 리프레시 토큰 타입 누락",
                                description = "리프레시 토큰(refreshToken)에서 타입(type)를 확인할 수 없는 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "유효하지 않은 토큰입니다.",
                                        "serverMessage": "Token type is missing.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "[REFRESH_TOKEN] 리프레시 토큰 타입 불일치",
                                description = "리프레시 토큰(refreshToken)이 아닌 다른 타입의 토큰으로 인증 요청한 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "유효하지 않은 토큰입니다.",
                                        "serverMessage": "Token is not an refresh token.",
                                        "data": null
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "[REFRESH_TOKEN] 리프레시 토큰 불일치",
                                description = "요청한 리프레시 토큰(refreshToken)이 등록된 기기의 리프레시 토큰과 일치하지 않는 경우 발생합니다.",
                                value = """
                                    {
                                        "clientMessage": "유효하지 않은 토큰입니다.",
                                        "serverMessage": "Refresh token does not match.",
                                        "data": null
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/token")
    suspend fun issueToken(
        @RequestBody issueTokenRequest: IssueTokenRequest
    ): ResponseEntity<ApiResponse<IssueTokenResponse>> {
        val issueTokenRequestModel = IssueTokenRequestModel.of(
            issueTokenRequest.grantType,
            issueTokenRequest.uuid,
            issueTokenRequest.secret,
            issueTokenRequest.refreshToken
        )
        val issueTokenResponse = authService.issueToken(issueTokenRequestModel)

        return ResponseEntity.ok(
            ApiResponse(
                "갱신되었습니다.",
                "SUCCESS",
                issueTokenResponse
            )
        )
    }
}