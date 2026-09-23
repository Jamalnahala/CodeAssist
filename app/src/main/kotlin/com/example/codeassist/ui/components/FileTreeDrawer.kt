package com.example.codeassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
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
import com.example.codeassist.model.CodeFile
import com.example.codeassist.model.FileType
import com.example.codeassist.model.Project
import com.example.codeassist.ui.theme.*

@Composable
fun FileTreeDrawer(
    currentProject: Project,
    activeFile: CodeFile?,
    onSelectFile: (CodeFile) -> Unit,
    onAddFile: (String) -> Unit,
    onDeleteFile: (CodeFile) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddFileDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .testTag("file_tree_drawer"),
        color = IdeSurfaceDark,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header: Project name + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EXPLORER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IdePrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = currentProject.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(
                        onClick = { showAddFileDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_new_file")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAdd,
                            contentDescription = "New File",
                            tint = IdePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search files…", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("input_search_files"),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(16.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IdePrimary,
                    unfocusedBorderColor = IdeBorderDark,
                    focusedContainerColor = IdeSurfaceVariantDark,
                    unfocusedContainerColor = IdeSurfaceVariantDark
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // File items
            val filteredFiles = currentProject.rootFiles.filter {
                it.name.contains(searchQuery, ignoreCase = true) || it.path.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        text = "PROJECT FILES (${filteredFiles.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(filteredFiles, key = { it.id }) { file ->
                    val isSelected = activeFile?.id == file.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) IdePrimary.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { onSelectFile(file) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("file_item_${file.name}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FileIcon(fileType = file.fileType)

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) IdePrimary else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (file.path != file.name) {
                                Text(
                                    text = file.path,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (file.isDirty) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(IdeAccentAmber)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteFile(file) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = IdeBorderDark, modifier = Modifier.padding(vertical = 8.dp))

            // Project info footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = currentProject.type.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IdePrimary
                    )
                    Text(
                        text = "SDK: ${currentProject.minSdk} -> 36",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "AGP 9.1.1",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showAddFileDialog) {
        AlertDialog(
            onDismissRequest = { showAddFileDialog = false },
            title = { Text("Create New File") },
            text = {
                Column {
                    Text("Enter filename with extension (e.g. MyScreen.kt, layout.xml):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        placeholder = { Text("FileName.kt") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_file_name")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            onAddFile(newFileName.trim())
                            newFileName = ""
                            showAddFileDialog = false
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_add_file")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FileIcon(fileType: FileType) {
    val (color, icon) = when (fileType) {
        FileType.KOTLIN -> Pair(Color(0xFF7F52FF), Icons.AutoMirrored.Filled.InsertDriveFile)
        FileType.JAVA -> Pair(Color(0xFFE76F00), Icons.Default.Coffee)
        FileType.XML -> Pair(Color(0xFFE44D26), Icons.Default.Code)
        FileType.GRADLE -> Pair(Color(0xFF02303A), Icons.Default.Build)
        FileType.JSON -> Pair(Color(0xFFF7DF1E), Icons.Default.DataObject)
        FileType.MARKDOWN -> Pair(Color(0xFF083FA1), Icons.Default.Description)
        else -> Pair(Color(0xFF888888), Icons.AutoMirrored.Filled.InsertDriveFile)
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(18.dp)
    )
}
