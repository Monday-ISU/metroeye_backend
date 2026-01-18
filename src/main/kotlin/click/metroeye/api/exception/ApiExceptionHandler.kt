package click.metroeye.api.exception

import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["click.metroeye.api.presentation.v1.controller"])
class ApiExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun exception(e: Exception): ResponseEntity<ApiResponse<Void?>> {
        return when (e) {
            is ApiException -> {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                        ApiResponse(
                            e.clientMessage,
                            e.serverMessage,
                            null
                        )
                    )
            }

            else -> {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        ApiResponse(
                            "서버 오류가 발생했습니다.",
                            e.message ?: "UNKNOWN SERVER ERROR",
                            null
                        )
                    )
            }
        }
    }
}