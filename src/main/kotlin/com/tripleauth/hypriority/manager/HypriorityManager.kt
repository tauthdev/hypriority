package com.tripleauth.hypriority.manager

import com.tripleauth.hypriority.codec.CustomJsonCodec
import com.tripleauth.hypriority.model.Job
import com.tripleauth.hypriority.model.JobPriority
import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.api.RPriorityQueue
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Component
class HypriorityManager(
    private val redissonClient: RedissonClient,
) {

    private val logger = KotlinLogging.logger {}

    fun <T> addJob(
        queueName: String,
        jobId: String,
        data: T,
        priority: JobPriority
    ) {
        val job = Job(id = jobId, priority = priority, data = data)
        val queue: RPriorityQueue<Job<T>> = redissonClient.getPriorityQueue(queueName, CustomJsonCodec())
        queue.trySetComparator(compareBy { it.priority.level })
        queue.add(job)

        logger.info { "Added job with priority ${priority.name} to queue $queueName: $job" }
    }

    fun <T> pollJob(queueName: String): Job<T>? {
        val queue: RPriorityQueue<Job<T>> = redissonClient.getPriorityQueue(queueName, CustomJsonCodec())

        return queue.poll()
    }
}