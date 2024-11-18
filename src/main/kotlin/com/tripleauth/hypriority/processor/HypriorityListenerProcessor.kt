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

                repeat(concurrency) {
                    thread(start = true) {
                        while (true) {
                            val job = manager.pollJob<Any>(queueName)
                            if (job != null) {
                                try {
                                    method.invoke(bean, job)
                                } catch (e: Exception) {
                                    logger.error { "Error processing job: ${e.message}" }
                                }
                            } else {
                                Thread.sleep(1000)
                            }
                        }
                    }
                }
            }
        }

        return bean
    }
}