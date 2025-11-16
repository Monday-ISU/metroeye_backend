package click.metroeye.api.infrastructure.client.seoul.dto

import org.apache.logging.log4j.message.Message

data class SeoulSubwayApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
