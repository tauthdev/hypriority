package com.tripleauth.hypriority.processor

import com.tripleauth.hypriority.annotation.HypriorityListener
import com.tripleauth.hypriority.manager.HypriorityManager
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Component
class HypriorityListenerProcessor(
    private val manager: HypriorityManager
) : BeanPostProcessor {

    private val logger = KotlinLogging.logger {}

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        bean::class.java.methods.forEach { method ->
            val annotation = method.getAnnotation(HypriorityListener::class.java)
            if (annotation != null) {
                val queueName = annotation.queueName
                val concurrency = annotation.concurrency

                repeat(concurrency.coerceAtMost(5)) {
                    thread(start = true) {
                        processQueue(queueName, bean, method)
                    }
                }
            }
        }

        return bean
    }

    private fun processQueue(queueName: String, bean: Any, method: java.lang.reflect.Method) {
        while (true) {
            try {
                val job = manager.pollJob<Any>(queueName)
                if (job != null) {
                    try {
                        method.invoke(bean, job)
                        logger.info { "Processed job from queue: $queueName" }
                    } catch (e: Exception) {
                        logger.error(e) { "Error processing job from queue: $queueName" }
                    }
                } else {
                    logger.debug { "No job available in queue: $queueName. Waiting..." }
                    Thread.sleep(1000)
                }
            } catch (e: Exception) {
                logger.error(e) { "Unexpected error while processing queue: $queueName" }
                Thread.sleep(5000)
            }
        }
    }
}
