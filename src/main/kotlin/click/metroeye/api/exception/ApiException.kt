package click.metroeye.api.exception

import org.springframework.http.HttpStatus

class ApiException(
    val status: HttpStatus,
    val clientMessage: String,
    val serverMessage: String
) : RuntimeException(serverMessage) {}