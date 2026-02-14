package click.metroeye.api.properties

data class R2dbcConnectionProperties(
    val url: String,
    val username: String,
    val password: String,
    val pool: R2dbcPoolProperties
)
