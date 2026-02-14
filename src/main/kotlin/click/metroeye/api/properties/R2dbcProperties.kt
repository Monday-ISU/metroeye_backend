package click.metroeye.api.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.r2dbc")
data class R2dbcProperties(
    val master: R2dbcConnectionProperties,
    val slave: R2dbcConnectionProperties
)
