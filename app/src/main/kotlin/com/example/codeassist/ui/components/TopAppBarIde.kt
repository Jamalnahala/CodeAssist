package com.example.codeassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeassist.model.Project
import com.example.codeassist.ui.theme.IdeAccentGreen
import com.example.codeassist.ui.theme.IdeBorderDark
import com.example.codeassist.ui.theme.IdePrimary
import com.example.codeassist.ui.theme.IdeSurfaceVariantDark
import com.example.codeassist.viewmodel.ActiveViewMode
import com.example.codeassist.viewmodel.BottomPanelTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarIde(
    currentProject: Project,
    projects: List<Project>,
    activeMode: ActiveViewMode,
    isBuilding: Boolean,
    bottomTab: BottomPanelTab,
    onOpenDrawer: () -> Unit,
    onSelectProject: (Project) -> Unit,
    onNewProject: () -> Unit,
    onSelectMode: (ActiveViewMode) -> Unit,
    onRunBuild: () -> Unit,
    onToggleBottomPanel: (BottomPanelTab) -> Unit,
    onOpenAi: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showProjectDropdown by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ide_top_app_bar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Drawer menu & Project selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("btn_open_file_tree")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Project Tree",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(IdeSurfaceVariantDark)
                                .clickable { showProjectDropdown = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("project_selector_dropdown")
                        ) {
                            Text(
                                text = currentProject.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Project",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showProjectDropdown,
                            onDismissRequest = { showProjectDropdown = false }
                        ) {
                            Text(
                                "Projects",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            projects.forEach { proj ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(proj.name, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                proj.type.displayName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onSelectProject(proj)
                                        showProjectDropdown = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (proj.id == currentProject.id) Icons.Default.Check else Icons.Default.Folder,
                                            contentDescription = null,
                                            tint = if (proj.id == currentProject.id) IdePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("New Project…") },
                                onClick = {
                                    showProjectDropdown = false
                                    onNewProject()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Add, contentDescription = "New Project")
                                }
                            )
                        }
                    }
                }

                // Middle: View Mode Selector (Code / Blocks / Preview)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IdeSurfaceVariantDark)
                        .padding(2.dp)
                ) {
                    ModeTab(
                        title = "Code",
                        isSelected = activeMode == ActiveViewMode.CODE,
                        onClick = { onSelectMode(ActiveViewMode.CODE) },
                        testTag = "tab_mode_code"
                    )
                    ModeTab(
                        title = "Blocks",
                        isSelected = activeMode == ActiveViewMode.BLOCKS,
                        onClick = { onSelectMode(ActiveViewMode.BLOCKS) },
                        testTag = "tab_mode_blocks"
                    )
                    ModeTab(
                        title = "Preview",
                        isSelected = activeMode == ActiveViewMode.PREVIEW,
                        onClick = { onSelectMode(ActiveViewMode.PREVIEW) },
                        testTag = "tab_mode_preview"
                    )
                }

                // Right: Action buttons (Build DAG, Git, AI, Settings)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Build & Run button
                    Button(
                        onClick = onRunBuild,
                        enabled = !isBuilding,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_run_build"),
                        colors = ButtonDefaults.buttonColors(containerColor = IdeAccentGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        if (isBuilding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Build & Run APK",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Build", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Git button
                    IconButton(
                        onClick = { onToggleBottomPanel(BottomPanelTab.GIT) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_toggle_git")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Commit,
                            contentDescription = "Version Control",
                            tint = if (bottomTab == BottomPanelTab.GIT) IdePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Terminal / Build log button
                    IconButton(
                        onClick = { onToggleBottomPanel(BottomPanelTab.TERMINAL) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_toggle_terminal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Build Console",
                            tint = if (bottomTab == BottomPanelTab.TERMINAL) IdePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // AI Assistant button
                    IconButton(
                        onClick = onOpenAi,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_open_ai")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = IdePrimary
                        )
                    }

                    // Settings button
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_open_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) IdePrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
