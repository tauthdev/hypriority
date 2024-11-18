package com.tripleauth.hypriority.model

interface PriorityJob {
    val priority: JobPriority
}

data class Job<T>(
    val id: String,
    override val priority: JobPriority,
    val data: T
) : PriorityJob