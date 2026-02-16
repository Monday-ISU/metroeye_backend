package click.metroeye.api.presentation.v1.dto.response

data class ApiResponse<T>(
    val clientMessage: String?,
    val serverMessage: String,
    val data: T?
)