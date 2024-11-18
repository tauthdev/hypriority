package com.tripleauth.hypriority.config

import com.tripleauth.hypriority.manager.HypriorityManager
import com.tripleauth.hypriority.processor.HypriorityListenerProcessor
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HypriorityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun hypriorityProperties(): HypriorityProperties {
        return HypriorityProperties()
    }

    @Bean
    @ConditionalOnMissingBean
    fun hypriorityManager(redissonClient: RedissonClient): HypriorityManager {
        return HypriorityManager(redissonClient)
    }

    @Bean
    @ConditionalOnMissingBean
    fun hypriorityListenerProcessor(manager: HypriorityManager): HypriorityListenerProcessor {
        return HypriorityListenerProcessor(manager)
    }
}
