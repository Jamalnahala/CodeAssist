package com.example.codeassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeassist.model.BuildLog
import com.example.codeassist.model.BuildResult
import com.example.codeassist.model.BuildTask
import com.example.codeassist.model.LogLevel
import com.example.codeassist.model.TaskStatus
import com.example.codeassist.ui.theme.*

@Composable
fun BuildConsoleView(
    tasks: List<BuildTask>,
    logs: List<BuildLog>,
    isBuilding: Boolean,
    buildResult: BuildResult?,
    onClearLogs: () -> Unit,
    onRunBuild: () -> Unit,
    onInstallApk: (String) -> Unit,
    onClosePanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLogLevel by remember { mutableStateOf<LogLevel?>(null) }
    var logSearch by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("build_console_view"),
        color = IdeSurfaceDark,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IdeSurfaceVariantDark)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = IdePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BUILD CONSOLE & TASK ENGINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isBuilding) {
                        Spacer(modifier = Modifier.width(10.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = IdeAccentAmber,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Building…", fontSize = 11.sp, color = IdeAccentAmber)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClearLogs, modifier = Modifier.size(28.dp).testTag("btn_clear_logs")) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Logs", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onClosePanel, modifier = Modifier.size(28.dp).testTag("btn_close_console")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Task Pipeline DAG strip
            if (tasks.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IdeBackgroundDark)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskBadge(task = task)
                    }
                }
                HorizontalDivider(color = IdeBorderDark)
            }

            // Success Banner with Install button
            if (buildResult != null && buildResult.isSuccess) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = IdeAccentGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IdeAccentGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IdeAccentGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Build Finished Successfully in ${buildResult.totalTimeMs}ms", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IdeAccentGreen)
                                Text("APK size: ${buildResult.apkSizeBytes / 1024 / 1024} MB", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = { buildResult.apkPath?.let { onInstallApk(it) } },
                            modifier = Modifier.height(30.dp).testTag("btn_install_apk"),
                            colors = ButtonDefaults.buttonColors(containerColor = IdeAccentGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Android, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Install APK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Log Console Output
            val filteredLogs = logs.filter { log ->
                (selectedLogLevel == null || log.level == selectedLogLevel) &&
                        (logSearch.isEmpty() || log.message.contains(logSearch, ignoreCase = true))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(EditorBackground)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (filteredLogs.isEmpty()) {
                    item {
                        Text(
                            text = if (isBuilding) "Executing build DAG..." else "No build logs yet. Tap 'Build' to run the on-device toolchain.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = EditorLineNumber
                        )
                    }
                }

                items(filteredLogs) { log ->
                    val color = when (log.level) {
                        LogLevel.VERBOSE -> Color(0xFF6B7280)
                        LogLevel.DEBUG -> Color(0xFF3B82F6)
                        LogLevel.INFO -> Color(0xFF10B981)
                        LogLevel.WARN -> Color(0xFFF59E0B)
                        LogLevel.ERROR -> Color(0xFFEF4444)
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = log.timestamp,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = EditorLineNumber,
                            modifier = Modifier.width(72.dp)
                        )
                        Text(
                            text = "[${log.level.name}] ",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Text(
                            text = log.message,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskBadge(task: BuildTask) {
    val (statusColor, icon) = when (task.status) {
        TaskStatus.PENDING -> Pair(Color(0xFF6B7280), Icons.Default.Schedule)
        TaskStatus.RUNNING -> Pair(IdeAccentAmber, Icons.Default.Sync)
        TaskStatus.SUCCESS -> Pair(IdeAccentGreen, Icons.Default.Check)
        TaskStatus.CACHED -> Pair(IdeAccentCyan, Icons.Default.Inventory2)
        TaskStatus.FAILED -> Pair(IdeAccentRed, Icons.Default.Close)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(statusColor.copy(alpha = 0.15f))
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = task.name,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = statusColor
        )
    }
}
