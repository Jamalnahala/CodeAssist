package com.example.codeassist.model

enum class ProjectType(val displayName: String, val badge: String) {
    ANDROID_COMPOSE("Android Jetpack Compose", "Compose"),
    ANDROID_VIEWS("Android XML Views", "Android"),
    KOTLIN_CONSOLE("Kotlin Application", "Kotlin"),
    JAVA_CONSOLE("Java Application", "Java")
}

data class Project(
    val id: String,
    val name: String,
    val packageName: String,
    val type: ProjectType,
    val minSdk: Int = 26,
    val targetSdk: Int = 36,
    val rootFiles: List<CodeFile> = emptyList(),
    val lastModified: Long = System.currentTimeMillis()
)
