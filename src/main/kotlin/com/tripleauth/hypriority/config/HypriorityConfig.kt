package com.tripleauth.hypriority.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.tripleauth.hypriority.codec.CustomJsonCodec
import com.tripleauth.hypriority.manager.HypriorityManager
import com.tripleauth.hypriority.processor.HypriorityListenerProcessor
import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.JsonJacksonCodec
import org.redisson.config.Config
import org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Role

@Configuration
class HypriorityConfig(
    private val properties: HypriorityProperties
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    fun redissonClient(): RedissonClient {
        val config = Config()
        val redisAddress = "redis://${properties.host}:${properties.port}"

        val objectMapper = ObjectMapper().apply {
            setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            deactivateDefaultTyping()
        }

        config.useSingleServer()
            .setAddress(redisAddress)
            .setDatabase(properties.database)
            .setConnectionPoolSize(properties.connectionPoolSize)
            .setConnectionMinimumIdleSize(properties.connectionMinimumIdleSize)
            .setTimeout(properties.timeout)

        config.codec = JsonJacksonCodec(objectMapper)

        logger.info { "[Hypriority] Connected to Redis at $redisAddress" }

        return Redisson.create(config)
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    fun hypriorityManager(redissonClient: RedissonClient): HypriorityManager {
        return HypriorityManager(redissonClient)
    }

    @Bean
    @DependsOn("redissonClient", "hypriorityManager")
    fun hypriorityListenerProcessor(manager: HypriorityManager): HypriorityListenerProcessor {
        return HypriorityListenerProcessor(manager)
    }

}
