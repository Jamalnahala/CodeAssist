package com.example.codeassist.model

enum class TaskStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    CACHED,
    FAILED
}

enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}

data class BuildLog(
    val timestamp: String,
    val level: LogLevel,
    val message: String,
    val taskName: String? = null
)

data class BuildTask(
    val id: String,
    val name: String,
    val description: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val durationMs: Long = 0L,
    val errorDetails: String? = null
)

data class BuildResult(
    val isSuccess: Boolean,
    val totalTimeMs: Long,
    val apkPath: String? = null,
    val apkSizeBytes: Long = 0L,
    val apkPackageName: String? = null,
    val tasksCount: Int = 0
)
