package click.metroeye.api.infrastructure.external.seoul.dto

import org.apache.logging.log4j.message.Message

data class SeoulSubwayApiResponse<out T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
