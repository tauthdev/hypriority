package com.tripleauth.hypriority.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "hypriority.redis")
data class HypriorityProperties(
    var host: String = "localhost",
    var port: Int = 6379,
    var database: Int = 0,
    var timeout: Int = 3000,
    var connectionPoolSize: Int = 64,
    var connectionMinimumIdleSize: Int = 10
)