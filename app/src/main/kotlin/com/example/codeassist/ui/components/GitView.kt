package com.example.codeassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.codeassist.model.GitChangedFile
import com.example.codeassist.model.GitCommit
import com.example.codeassist.ui.theme.*

@Composable
fun GitView(
    changedFiles: List<GitChangedFile>,
    commits: List<GitCommit>,
    currentBranch: String,
    onStageFile: (String) -> Unit,
    onStageAll: () -> Unit,
    onCommit: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var commitMessage by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Changes, 1: History

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .testTag("git_view_panel"),
        color = IdeSurfaceDark,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                        imageVector = Icons.Default.Commit,
                        contentDescription = null,
                        tint = IdePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VERSION CONTROL (GIT)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Branch chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(IdePrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = " $currentBranch",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = IdePrimary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.width(0.dp),
                        color = Color.Transparent
                    )
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp).testTag("btn_close_git")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Tab bar (Changes vs History)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = IdeSurfaceDark,
                contentColor = IdePrimary,
                modifier = Modifier.height(36.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Working Tree (${changedFiles.size})", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Commit Log (${commits.size})", fontSize = 11.sp) }
                )
            }

            if (selectedTab == 0) {
                // Changes Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Commit Input Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commitMessage,
                            onValueChange = { commitMessage = it },
                            placeholder = { Text("Commit message…", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("input_commit_message"),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IdePrimary,
                                unfocusedBorderColor = IdeBorderDark,
                                focusedContainerColor = IdeSurfaceVariantDark,
                                unfocusedContainerColor = IdeSurfaceVariantDark
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onCommit(commitMessage.trim())
                                commitMessage = ""
                            },
                            enabled = commitMessage.isNotBlank() && changedFiles.any { it.isStaged },
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("btn_commit"),
                            colors = ButtonDefaults.buttonColors(containerColor = IdePrimary)
                        ) {
                            Text("Commit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stage All Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHANGED FILES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = onStageAll,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp).testTag("btn_stage_all")
                        ) {
                            Text("Stage / Unstage All", fontSize = 11.sp, color = IdePrimary)
                        }
                    }

                    // Files List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (changedFiles.isEmpty()) {
                            item {
                                Text(
                                    text = "Working tree clean. No changes to commit.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        items(changedFiles, key = { it.path }) { file ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(IdeSurfaceVariantDark)
                                    .clickable { onStageFile(file.path) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("git_file_${file.path}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = file.isStaged,
                                        onCheckedChange = { onStageFile(file.path) },
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = file.path,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (file.isStaged) "(staged)" else "(unstaged)",
                                        fontSize = 10.sp,
                                        color = if (file.isStaged) IdeAccentGreen else IdeAccentAmber
                                    )
                                }

                                Row {
                                    Text("+${file.diffAdditions}", fontSize = 10.sp, color = IdeAccentGreen, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("-${file.diffDeletions}", fontSize = 10.sp, color = IdeAccentRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // History Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(commits, key = { it.hash }) { commit ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = IdeSurfaceVariantDark,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(commit.message, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(commit.hash, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = IdePrimary)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${commit.author} • ${commit.timestamp}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${commit.filesChangedCount} files changed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
