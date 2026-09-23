package com.example.codeassist.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.codeassist.ui.components.*
import com.example.codeassist.ui.theme.EditorBackground
import com.example.codeassist.viewmodel.ActiveViewMode
import com.example.codeassist.viewmodel.BottomPanelTab
import com.example.codeassist.viewmodel.IdeViewModel

@Composable
fun CodeAssistApp(
    viewModel: IdeViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject by viewModel.currentProject.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val openFiles by viewModel.openFiles.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()
    val activeViewMode by viewModel.activeViewMode.collectAsState()
    val bottomPanelTab by viewModel.bottomPanelTab.collectAsState()
    val buildTasks by viewModel.buildTasks.collectAsState()
    val buildLogs by viewModel.buildLogs.collectAsState()
    val isBuilding by viewModel.isBuilding.collectAsState()
    val buildResult by viewModel.buildResult.collectAsState()
    val gitFiles by viewModel.gitFiles.collectAsState()
    val gitCommits by viewModel.gitCommits.collectAsState()
    val currentBranch by viewModel.currentBranch.collectAsState()

    val showNewProjectDialog by viewModel.showNewProjectDialog.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val showAiSheet by viewModel.showAiSheet.collectAsState()
    val showRunApkDialog by viewModel.showRunApkDialog.collectAsState()
    val editorFontSize by viewModel.editorFontSize.collectAsState()
    val showLineNumbers by viewModel.showLineNumbers.collectAsState()

    var isDrawerOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("code_assist_main_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBarIde(
                currentProject = currentProject,
                projects = projects,
                activeMode = activeViewMode,
                isBuilding = isBuilding,
                bottomTab = bottomPanelTab,
                onOpenDrawer = { isDrawerOpen = !isDrawerOpen },
                onSelectProject = { viewModel.selectProject(it) },
                onNewProject = { viewModel.showNewProjectDialog.value = true },
                onSelectMode = { viewModel.setViewMode(it) },
                onRunBuild = { viewModel.runBuild() },
                onToggleBottomPanel = { viewModel.setBottomPanel(it) },
                onOpenAi = { viewModel.showAiSheet.value = true },
                onOpenSettings = { viewModel.showSettingsDialog.value = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(EditorBackground)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Lateral Project Explorer Drawer
                AnimatedVisibility(
                    visible = isDrawerOpen,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    FileTreeDrawer(
                        currentProject = currentProject,
                        activeFile = activeFile,
                        onSelectFile = {
                            viewModel.openFile(it)
                            isDrawerOpen = false
                        },
                        onAddFile = { fileName ->
                            viewModel.addNewFile(fileName)
                            isDrawerOpen = false
                        },
                        onDeleteFile = { viewModel.deleteFile(it) },
                        onCloseDrawer = { isDrawerOpen = false }
                    )
                }

                // Main Editor / Work Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (activeViewMode) {
                            ActiveViewMode.CODE -> {
                                CodeEditorView(
                                    openFiles = openFiles,
                                    activeFile = activeFile,
                                    fontSizeSp = editorFontSize,
                                    showLineNumbers = showLineNumbers,
                                    onSelectFile = { viewModel.openFile(it) },
                                    onCloseFile = { viewModel.closeFile(it) },
                                    onContentChange = { viewModel.updateActiveFileContent(it) },
                                    onUndo = { viewModel.undo() },
                                    onRedo = { viewModel.redo() }
                                )
                            }
                            ActiveViewMode.BLOCKS -> {
                                BlockEditorView(
                                    activeFile = activeFile,
                                    onSyncCode = {
                                        viewModel.updateActiveFileContent(it)
                                        viewModel.setViewMode(ActiveViewMode.CODE)
                                    }
                                )
                            }
                            ActiveViewMode.PREVIEW -> {
                                LayoutPreviewView(
                                    activeFile = activeFile
                                )
                            }
                        }
                    }

                    // Bottom Panels (Build Console / Terminal, Git, etc.)
                    AnimatedVisibility(
                        visible = bottomPanelTab != BottomPanelTab.NONE,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        when (bottomPanelTab) {
                            BottomPanelTab.TERMINAL -> {
                                BuildConsoleView(
                                    tasks = buildTasks,
                                    logs = buildLogs,
                                    isBuilding = isBuilding,
                                    buildResult = buildResult,
                                    onClearLogs = { viewModel.clearLogs() },
                                    onRunBuild = { viewModel.runBuild() },
                                    onInstallApk = { viewModel.showRunApkDialog.value = true },
                                    onClosePanel = { viewModel.setBottomPanel(BottomPanelTab.NONE) }
                                )
                            }
                            BottomPanelTab.GIT -> {
                                GitView(
                                    changedFiles = gitFiles,
                                    commits = gitCommits,
                                    currentBranch = currentBranch,
                                    onStageFile = { viewModel.stageFile(it) },
                                    onStageAll = { viewModel.stageAllFiles() },
                                    onCommit = { viewModel.commitChanges(it) },
                                    onClose = { viewModel.setBottomPanel(BottomPanelTab.NONE) }
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    // Dialogs & Modals
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { viewModel.showNewProjectDialog.value = false },
            onCreate = { name, pkg, type, minSdk ->
                viewModel.createProject(name, pkg, type, minSdk)
                viewModel.showNewProjectDialog.value = false
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            fontSize = editorFontSize,
            showLineNumbers = showLineNumbers,
            onFontSizeChange = { viewModel.editorFontSize.value = it },
            onToggleLineNumbers = { viewModel.showLineNumbers.value = !viewModel.showLineNumbers.value },
            onDismiss = { viewModel.showSettingsDialog.value = false }
        )
    }

    if (showAiSheet) {
        AiAssistantSheet(
            activeFile = activeFile,
            onApplyCode = {
                viewModel.updateActiveFileContent(it)
                viewModel.showAiSheet.value = false
            },
            onDismiss = { viewModel.showAiSheet.value = false }
        )
    }

    if (showRunApkDialog && buildResult != null) {
        RunApkDialog(
            buildResult = buildResult!!,
            onDismiss = { viewModel.showRunApkDialog.value = false }
        )
    }
}
