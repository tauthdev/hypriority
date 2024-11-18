package com.tripleauth.hypriority.logger

import com.tripleauth.hypriority.annotation.HypriorityListener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class HypriorityListenerLogger : BeanPostProcessor {

    private val logger = KotlinLogging.logger {}

    override fun postProcessAfterInitialization(
        bean: Any,
        beanName: String
    ): Any {
        val methods: Array<Method> = bean::class.java.declaredMethods

        methods.forEach { method ->
            val annotation = method.getAnnotation(HypriorityListener::class.java)
            if (annotation != null) {
                val queueName = annotation.queueName
                val concurrency = annotation.concurrency
                logger.info { "Listener registered: Method = ${method.name}, Queue = $queueName, Concurrency = $concurrency" }
            }
        }

        return bean
    }

}
