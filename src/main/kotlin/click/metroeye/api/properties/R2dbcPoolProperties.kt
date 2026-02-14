package click.metroeye.api.properties

import java.time.Duration

data class R2dbcPoolProperties(
    val enabled: Boolean,
    val initialSize: Int,
    val maxIdleTime: Duration,
    val maxLifeTime: Duration,
    val validationQuery: String
)
