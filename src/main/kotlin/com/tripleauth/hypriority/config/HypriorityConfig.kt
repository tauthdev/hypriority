package com.tripleauth.hypriority.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HypriorityConfig(
    private val properties: HypriorityProperties
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val redisAddress = "redis://${properties.host}:${properties.port}"

        config.useSingleServer()
            .setAddress(redisAddress)
            .setDatabase(properties.database)
            .setConnectionPoolSize(properties.connectionPoolSize)
            .setConnectionMinimumIdleSize(properties.connectionMinimumIdleSize)
            .setTimeout(properties.timeout)

        logger.info { "[Hypriority] Connected to Redis at $redisAddress" }

        return Redisson.create(config)
    }

}