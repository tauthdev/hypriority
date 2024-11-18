package com.tripleauth.hypriority.model

enum class JobPriority(val level: Int) {
    HIGHEST(1),
    HIGH(2),
    NORMAL(3),
    LOW(4),
    LOWEST(5);

    companion object {
        fun fromLevel(level: Int): JobPriority {
            return values().find { it.level == level } ?: NORMAL
        }
    }
}