package com.example.codeassist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeassist.model.ProjectType
import com.example.codeassist.ui.theme.IdePrimary
import com.example.codeassist.ui.theme.IdeSurfaceVariantDark

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, packageName: String, type: ProjectType, minSdk: Int) -> Unit
) {
    var projectName by remember { mutableStateOf("MyAwesomeApp") }
    var packageName by remember { mutableStateOf("com.example.myawesomeapp") }
    var selectedType by remember { mutableStateOf(ProjectType.ANDROID_COMPOSE) }
    var minSdk by remember { mutableStateOf(26) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Project", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Project Name
                OutlinedTextField(
                    value = projectName,
                    onValueChange = {
                        projectName = it
                        packageName = "com.example.${it.lowercase().replace(" ", "")}"
                    },
                    label = { Text("Project Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_project_name")
                )

                // Package Name
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_package_name")
                )

                Text("Template Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                ProjectType.values().forEach { type ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type },
                        color = if (selectedType == type) IdePrimary.copy(alpha = 0.2f) else IdeSurfaceVariantDark,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(type.displayName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Native on-device template", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        onCreate(projectName.trim(), packageName.trim(), selectedType, minSdk)
                    }
                },
                modifier = Modifier.testTag("btn_confirm_create_project")
            ) {
                Text("Create Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
