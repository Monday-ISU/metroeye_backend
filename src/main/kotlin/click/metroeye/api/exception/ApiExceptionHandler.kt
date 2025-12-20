package click.metroeye.api.exception

import click.metroeye.api.presentation.v1.dto.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["click.metroeye.api.presentation.v1.controller"])
class ApiExceptionHandler {
    @ExceptionHandler(value = [
        ApiException::class
    ])
    fun apiException(apiException: ApiException): ResponseEntity<ApiResponse<Void?>> {
        return ResponseEntity.ok(ApiResponse(
            apiException.clientMessage,
            apiException.serverMessage,
            null
        ))
    }
}