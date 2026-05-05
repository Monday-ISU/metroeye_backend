package click.metroeye.api.config.properties

import io.r2dbc.pool.ConnectionPoolConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "spring.r2dbc")
data class R2dbcProperties(
    val master: ConnectionProperties,
    val slave: ConnectionProperties
) {
    data class ConnectionProperties(
        val url: String,
        val username: String,
        val password: String,
        val pool: PoolProperties
    )

    data class PoolProperties(
        val enabled: Boolean = true,
        val maxAcquireTime: Duration = ConnectionPoolConfiguration.NO_TIMEOUT,
        val maxLifeTime: Duration = ConnectionPoolConfiguration.NO_TIMEOUT,
        val maxIdleTime: Duration = Duration.ofMillis(30000),
        val validationQuery: String = "SELECT 1"
    )
}