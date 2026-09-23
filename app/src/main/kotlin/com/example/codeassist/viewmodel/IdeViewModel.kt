package com.example.codeassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeassist.data.SampleProjects
import com.example.codeassist.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class ActiveViewMode {
    CODE,
    BLOCKS,
    PREVIEW
}

enum class BottomPanelTab {
    NONE,
    TERMINAL,
    DIAGNOSTICS,
    GIT
}

class IdeViewModel : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(SampleProjects.all)
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow<Project>(SampleProjects.composeAppProject)
    val currentProject: StateFlow<Project> = _currentProject.asStateFlow()

    private val _openFiles = MutableStateFlow<List<CodeFile>>(emptyList())
    val openFiles: StateFlow<List<CodeFile>> = _openFiles.asStateFlow()

    private val _activeFile = MutableStateFlow<CodeFile?>(null)
    val activeFile: StateFlow<CodeFile?> = _activeFile.asStateFlow()

    private val _activeViewMode = MutableStateFlow(ActiveViewMode.CODE)
    val activeViewMode: StateFlow<ActiveViewMode> = _activeViewMode.asStateFlow()

    private val _bottomPanelTab = MutableStateFlow(BottomPanelTab.NONE)
    val bottomPanelTab: StateFlow<BottomPanelTab> = _bottomPanelTab.asStateFlow()

    private val _buildTasks = MutableStateFlow<List<BuildTask>>(emptyList())
    val buildTasks: StateFlow<List<BuildTask>> = _buildTasks.asStateFlow()

    private val _buildLogs = MutableStateFlow<List<BuildLog>>(emptyList())
    val buildLogs: StateFlow<List<BuildLog>> = _buildLogs.asStateFlow()

    private val _isBuilding = MutableStateFlow(false)
    val isBuilding: StateFlow<Boolean> = _isBuilding.asStateFlow()

    private val _buildResult = MutableStateFlow<BuildResult?>(null)
    val buildResult: StateFlow<BuildResult?> = _buildResult.asStateFlow()

    private val _gitFiles = MutableStateFlow<List<GitChangedFile>>(
        listOf(
            GitChangedFile("MainActivity.kt", GitFileStatus.MODIFIED, isStaged = false, diffAdditions = 12, diffDeletions = 4),
            GitChangedFile("build.gradle.kts", GitFileStatus.MODIFIED, isStaged = true, diffAdditions = 2, diffDeletions = 1)
        )
    )
    val gitFiles: StateFlow<List<GitChangedFile>> = _gitFiles.asStateFlow()

    private val _gitCommits = MutableStateFlow<List<GitCommit>>(
        listOf(
            GitCommit("a1b2c3d", "Initial project creation", "CodeAssist", "2 hours ago", 4),
            GitCommit("e5f6g7h", "Add M3 MaterialTheme & CounterScreen", "Developer", "1 hour ago", 2)
        )
    )
    val gitCommits: StateFlow<List<GitCommit>> = _gitCommits.asStateFlow()

    private val _currentBranch = MutableStateFlow("main")
    val currentBranch: StateFlow<String> = _currentBranch.asStateFlow()

    // Dialog & Sheet States
    val showNewProjectDialog = MutableStateFlow(false)
    val showSettingsDialog = MutableStateFlow(false)
    val showAiSheet = MutableStateFlow(false)
    val showRunApkDialog = MutableStateFlow(false)
    val showFindInFile = MutableStateFlow(false)

    // Settings
    val editorFontSize = MutableStateFlow(14)
    val editorWrapLines = MutableStateFlow(false)
    val showLineNumbers = MutableStateFlow(true)
    val currentThemeName = MutableStateFlow("CodeAssist Dark")

    // Undo / Redo stacks for active file
    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    init {
        // Open the primary file of the starter project
        val initialFile = _currentProject.value.rootFiles.find { it.name.endsWith(".kt") }
            ?: _currentProject.value.rootFiles.firstOrNull()

        if (initialFile != null) {
            openFile(initialFile)
        }
    }

    fun openFile(file: CodeFile) {
        if (!_openFiles.value.any { it.id == file.id }) {
            _openFiles.value = _openFiles.value + file
        }
        _activeFile.value = file
        undoStack.clear()
        redoStack.clear()
    }

    fun closeFile(file: CodeFile) {
        val currentList = _openFiles.value.filter { it.id != file.id }
        _openFiles.value = currentList
        if (_activeFile.value?.id == file.id) {
            _activeFile.value = currentList.lastOrNull()
        }
    }

    fun updateActiveFileContent(newContent: String) {
        val current = _activeFile.value ?: return
        if (current.content != newContent) {
            undoStack.add(current.content)
            redoStack.clear()
            if (undoStack.size > 50) undoStack.removeAt(0)

            val updated = current.copy(content = newContent, isDirty = true)
            _activeFile.value = updated

            // Also update in open files and project root
            _openFiles.value = _openFiles.value.map { if (it.id == updated.id) updated else it }
            _currentProject.value = _currentProject.value.copy(
                rootFiles = _currentProject.value.rootFiles.map { if (it.id == updated.id) updated else it }
            )
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.size - 1)
            val current = _activeFile.value ?: return
            redoStack.add(current.content)
            val updated = current.copy(content = previous, isDirty = true)
            _activeFile.value = updated
            _openFiles.value = _openFiles.value.map { if (it.id == updated.id) updated else it }
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.size - 1)
            val current = _activeFile.value ?: return
            undoStack.add(current.content)
            val updated = current.copy(content = next, isDirty = true)
            _activeFile.value = updated
            _openFiles.value = _openFiles.value.map { if (it.id == updated.id) updated else it }
        }
    }

    fun setViewMode(mode: ActiveViewMode) {
        _activeViewMode.value = mode
    }

    fun setBottomPanel(tab: BottomPanelTab) {
        _bottomPanelTab.value = if (_bottomPanelTab.value == tab) BottomPanelTab.NONE else tab
    }

    fun selectProject(project: Project) {
        _currentProject.value = project
        _openFiles.value = emptyList()
        val firstFile = project.rootFiles.find { it.name.endsWith(".kt") || it.name.endsWith(".java") }
            ?: project.rootFiles.firstOrNull()
        if (firstFile != null) {
            openFile(firstFile)
        } else {
            _activeFile.value = null
        }
    }

    fun createProject(name: String, packageName: String, type: ProjectType, minSdk: Int) {
        val newProj = Project(
            id = "proj_${System.currentTimeMillis()}",
            name = name,
            packageName = packageName,
            type = type,
            minSdk = minSdk,
            targetSdk = 36,
            rootFiles = listOf(
                CodeFile(
                    id = "f_main",
                    name = if (type == ProjectType.JAVA_CONSOLE) "Main.java" else "MainActivity.kt",
                    path = "src/main/kotlin/$name/MainActivity.kt",
                    content = if (type == ProjectType.ANDROID_COMPOSE) {
                        """package $packageName

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Hello from $name!", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}"""
                    } else {
                        """package $packageName

fun main() {
    println("Hello from $name running on CodeAssist!")
}"""
                    }
                ),
                CodeFile(
                    id = "f_gradle",
                    name = "build.gradle.kts",
                    path = "build.gradle.kts",
                    content = """plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}"""
                )
            )
        )
        _projects.value = _projects.value + newProj
        selectProject(newProj)
    }

    fun addNewFile(name: String, content: String = "") {
        val newFile = CodeFile(
            id = "file_${System.currentTimeMillis()}",
            name = name,
            path = name,
            content = content
        )
        val updatedFiles = _currentProject.value.rootFiles + newFile
        _currentProject.value = _currentProject.value.copy(rootFiles = updatedFiles)
        openFile(newFile)
    }

    fun deleteFile(file: CodeFile) {
        closeFile(file)
        val updatedFiles = _currentProject.value.rootFiles.filter { it.id != file.id }
        _currentProject.value = _currentProject.value.copy(rootFiles = updatedFiles)
    }

    // Build Execution
    fun runBuild() {
        if (_isBuilding.value) return
        _isBuilding.value = true
        _bottomPanelTab.value = BottomPanelTab.TERMINAL
        _buildResult.value = null

        val tasks = listOf(
            BuildTask("task_clean", ":app:clean", "Cleaning build outputs"),
            BuildTask("task_deps", ":app:checkDependencies", "Validating Gradle dependencies and SDK 36"),
            BuildTask("task_aapt", ":app:mergeResources", "Compiling AAPT2 Android resources & assets"),
            BuildTask("task_compile", ":app:compileKotlin", "Incremental Kotlinc/JDT compiler DAG execution"),
            BuildTask("task_dex", ":app:dexBuilder", "D8/R8 Dex bytecode generation"),
            BuildTask("task_package", ":app:packageDebug", "Packaging APK archive"),
            BuildTask("task_sign", ":app:signDebugApk", "Apksigner V2/V3 cryptographic signing")
        )

        _buildTasks.value = tasks
        _buildLogs.value = emptyList()

        appendLog(LogLevel.INFO, "Starting incremental on-device build for ${_currentProject.value.name}...")
        appendLog(LogLevel.DEBUG, "Target SDK: 36, Min SDK: ${_currentProject.value.minSdk}, Toolchain: AGP 9.1.1")

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            var hasFailure = false

            for (i in tasks.indices) {
                val current = tasks[i]
                _buildTasks.value = _buildTasks.value.mapIndexed { idx, t ->
                    if (idx == i) t.copy(status = TaskStatus.RUNNING) else t
                }
                appendLog(LogLevel.VERBOSE, "> Task ${current.name} EXECUTING")

                val stepDuration = (400..800).random().toLong()
                delay(stepDuration)

                _buildTasks.value = _buildTasks.value.mapIndexed { idx, t ->
                    if (idx == i) t.copy(status = TaskStatus.SUCCESS, durationMs = stepDuration) else t
                }
                appendLog(LogLevel.INFO, "> Task ${current.name} SUCCESS in ${stepDuration}ms")
            }

            val totalTime = System.currentTimeMillis() - startTime
            val apkSize = (4_250_000..5_100_000).random().toLong()
            val result = BuildResult(
                isSuccess = true,
                totalTimeMs = totalTime,
                apkPath = "/sdcard/CodeAssist/build/outputs/apk/debug/${_currentProject.value.name}-debug.apk",
                apkSizeBytes = apkSize,
                apkPackageName = _currentProject.value.packageName,
                tasksCount = tasks.size
            )
            _buildResult.value = result
            _isBuilding.value = false

            appendLog(LogLevel.INFO, "BUILD SUCCESSFUL in ${totalTime / 1000.0}s")
            appendLog(LogLevel.INFO, "APK: ${result.apkPath} (${apkSize / 1024 / 1024}MB)")
        }
    }

    private fun appendLog(level: LogLevel, message: String) {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        val log = BuildLog(
            timestamp = sdf.format(Date()),
            level = level,
            message = message
        )
        _buildLogs.value = _buildLogs.value + log
    }

    fun clearLogs() {
        _buildLogs.value = emptyList()
    }

    // Git Actions
    fun stageFile(path: String) {
        _gitFiles.value = _gitFiles.value.map {
            if (it.path == path) it.copy(isStaged = !it.isStaged) else it
        }
    }

    fun stageAllFiles() {
        val allStaged = _gitFiles.value.all { it.isStaged }
        _gitFiles.value = _gitFiles.value.map { it.copy(isStaged = !allStaged) }
    }

    fun commitChanges(message: String) {
        if (message.isBlank()) return
        val staged = _gitFiles.value.filter { it.isStaged }
        if (staged.isEmpty()) return

        val newCommit = GitCommit(
            hash = UUID.randomUUID().toString().take(7),
            message = message,
            author = "CodeAssist User",
            timestamp = "Just now",
            filesChangedCount = staged.size
        )
        _gitCommits.value = listOf(newCommit) + _gitCommits.value
        _gitFiles.value = _gitFiles.value.filterNot { it.isStaged }
    }
}
