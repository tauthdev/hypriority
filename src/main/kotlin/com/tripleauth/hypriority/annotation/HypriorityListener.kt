package com.tripleauth.hypriority.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HypriorityListener(
    val queueName: String,
    val concurrency: Int = 1
)