package click.metroeye.api.config.r2dbc

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.reactive.TransactionSynchronizationManager
import reactor.core.publisher.Mono

@Configuration
@EnableConfigurationProperties(R2dbcProperties::class)
class R2dbcConfig {
    @Bean("masterConnectionFactory")
    fun masterConnectionFactory(r2dbcProperties: R2dbcProperties): ConnectionFactory =
        buildConnectionFactory(r2dbcProperties.master)

    @Bean("slaveConnectionFactory")
    fun slaveConnectionFactory(r2dbcProperties: R2dbcProperties): ConnectionFactory =
        buildConnectionFactory(r2dbcProperties.slave)

    @Primary
    @Bean("routingConnectionFactory")
    fun routingConnectionFactory(
        @Qualifier("masterConnectionFactory") masterConnectionFactory: ConnectionFactory,
        @Qualifier("slaveConnectionFactory") slaveConnectionFactory: ConnectionFactory
    ): ConnectionFactory = buildRoutingConnectionFactory(masterConnectionFactory, slaveConnectionFactory)

    private fun buildConnectionFactory(instance: R2dbcProperties.ConnectionProperties): ConnectionFactory {
        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions
                .parse(instance.url)
                .mutate()
                .option(ConnectionFactoryOptions.USER, instance.username)
                .option(ConnectionFactoryOptions.PASSWORD, instance.password)
                .build()
        )

        return if (instance.pool.enabled) {
            ConnectionPool(
                ConnectionPoolConfiguration.builder(connectionFactory)
                    .maxAcquireTime(instance.pool.maxAcquireTime)
                    .maxLifeTime(instance.pool.maxLifeTime)
                    .maxIdleTime(instance.pool.maxIdleTime)
                    .validationQuery(instance.pool.validationQuery)
                    .build()
            )
        } else {
            connectionFactory
        }
    }

    private fun buildRoutingConnectionFactory(
        masterConnectionFactory: ConnectionFactory,
        slaveConnectionFactory: ConnectionFactory
    ): ConnectionFactory = object : AbstractRoutingConnectionFactory() {
        override fun determineCurrentLookupKey(): Mono<in Any> = Mono.deferContextual {
            Mono.just(it.getOrDefault("DB_TYPE", "MASTER") ?: "MASTER")
        }
    }.also {
        it.setTargetConnectionFactories(
            mapOf(
                "MASTER" to masterConnectionFactory,
                "SLAVE" to slaveConnectionFactory
            )
        )
        it.setDefaultTargetConnectionFactory(masterConnectionFactory)
        it.afterPropertiesSet()
    }

    @Bean
    fun transactionManager(
        @Qualifier("routingConnectionFactory") routingConnectionFactory: ConnectionFactory
    ): R2dbcTransactionManager = object : R2dbcTransactionManager(routingConnectionFactory) {
        override fun doBegin(
            synchronizationManager: TransactionSynchronizationManager,
            transaction: Any,
            definition: TransactionDefinition
        ): Mono<Void?> {
            val dbType = if (definition.isReadOnly) "SLAVE" else "MASTER"
            return super.doBegin(synchronizationManager, transaction, definition)
                .contextWrite {
                    it.put("DB_TYPE", dbType)
                }
        }
    }
}