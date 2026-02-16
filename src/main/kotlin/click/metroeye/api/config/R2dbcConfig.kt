package click.metroeye.api.config

import click.metroeye.api.properties.R2dbcProperties
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
    @Primary
    @Bean("routingConnectionFactory")
    fun routingConnectionFactory(
        @Qualifier("masterConnectionFactory") masterConnectionFactory: ConnectionFactory,
        @Qualifier("slaveConnectionFactory") slaveConnectionFactory: ConnectionFactory
    ): ConnectionFactory {
        val routingConnectionFactory = object : AbstractRoutingConnectionFactory() {
            override fun determineCurrentLookupKey(): Mono<Any> {
                return Mono.deferContextual { contextView ->
                    val dbType = contextView.getOrDefault("DB_TYPE", "MASTER") ?: "MASTER"
                    Mono.just(dbType)
                }
            }
        }

        routingConnectionFactory.setTargetConnectionFactories(
            mapOf(
                "MASTER" to masterConnectionFactory,
                "SLAVE" to slaveConnectionFactory
            )
        )

        routingConnectionFactory.setDefaultTargetConnectionFactory(masterConnectionFactory)
        routingConnectionFactory.afterPropertiesSet()
        return routingConnectionFactory
    }

    @Bean("masterConnectionFactory")
    fun masterConnectionFactory(r2dbcProperties: R2dbcProperties): ConnectionFactory {
        val master = r2dbcProperties.master
        val options = ConnectionFactoryOptions
            .parse(master.url)
            .mutate()
            .option(ConnectionFactoryOptions.USER, master.username)
            .option(ConnectionFactoryOptions.PASSWORD, master.password)
            .build()

        val connectionFactory = ConnectionFactories.get(options)

        return if (master.pool.enabled) {
            val pool = ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(master.pool.initialSize)
                .maxIdleTime(master.pool.maxIdleTime)
                .maxLifeTime(master.pool.maxLifeTime)
                .build()

            ConnectionPool(pool)
        } else {
            connectionFactory
        }
    }

    @Bean("slaveConnectionFactory")
    fun slaveConnectionFactory(r2dbcProperties: R2dbcProperties): ConnectionFactory {
        val slave = r2dbcProperties.slave
        val options = ConnectionFactoryOptions
            .parse(slave.url)
            .mutate()
            .option(ConnectionFactoryOptions.USER, slave.username)
            .option(ConnectionFactoryOptions.PASSWORD, slave.password)
            .build()

        val connectionFactory = ConnectionFactories.get(options)

        return if (slave.pool.enabled) {
            val pool = ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(slave.pool.initialSize)
                .maxIdleTime(slave.pool.maxIdleTime)
                .maxLifeTime(slave.pool.maxLifeTime)
                .build()

            ConnectionPool(pool)
        } else {
            connectionFactory
        }
    }

    @Bean
    fun transactionManager(
        @Qualifier("routingConnectionFactory") routingConnectionFactory: ConnectionFactory,
    ): R2dbcTransactionManager {
        return object: R2dbcTransactionManager(routingConnectionFactory) {
            override fun doBegin(
                synchronizationManager: TransactionSynchronizationManager,
                transaction: Any,
                definition: TransactionDefinition
            ): Mono<Void> {
                val dbType = if (definition.isReadOnly) "SLAVE" else "MASTER"
                return super.doBegin(synchronizationManager, transaction, definition)
                    .contextWrite { context ->
                        context.put("DB_TYPE", dbType)
                    }
            }
        }
    }
}