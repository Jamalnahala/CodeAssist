package com.example.codeassist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsDialog(
    fontSize: Int,
    showLineNumbers: Boolean,
    onFontSizeChange: (Int) -> Unit,
    onToggleLineNumbers: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("IDE Settings", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Font size slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Editor Font Size", fontSize = 13.sp)
                        Text("${fontSize}sp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Slider(
                        value = fontSize.toFloat(),
                        onValueChange = { onFontSizeChange(it.toInt()) },
                        valueRange = 10f..22f,
                        steps = 11,
                        modifier = Modifier.testTag("slider_font_size")
                    )
                }

                // Show Line Numbers switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Show Line Numbers", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Display gutter in code editor", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = showLineNumbers,
                        onCheckedChange = { onToggleLineNumbers() },
                        modifier = Modifier.testTag("switch_line_numbers")
                    )
                }

                HorizontalDivider()

                // Toolchain details
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ON-DEVICE TOOLCHAIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("• Android SDK: API Level 36 (Android 15)", fontSize = 11.sp)
                    Text("• Gradle: 9.3.1 (No daemon required)", fontSize = 11.sp)
                    Text("• Compiler: Kotlinc 2.2 + Eclipse JDT", fontSize = 11.sp)
                    Text("• Dexer: D8/R8 pure-Java in-process", fontSize = 11.sp)
                    Text("• Packager & Signer: Apksigner V2/V3", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.testTag("btn_close_settings")) {
                Text("Done")
            }
        }
    )
}
