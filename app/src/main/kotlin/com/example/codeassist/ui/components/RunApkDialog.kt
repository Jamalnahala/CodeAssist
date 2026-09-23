package com.example.codeassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeassist.model.BuildResult
import com.example.codeassist.ui.theme.IdeAccentGreen
import com.example.codeassist.ui.theme.IdeSurfaceVariantDark

@Composable
fun RunApkDialog(
    buildResult: BuildResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Android, contentDescription = null, tint = IdeAccentGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Install Built APK", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = IdeSurfaceVariantDark
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IdeAccentGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Package ready for installation", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        Text(
                            text = "Path: ${buildResult.apkPath ?: "app-debug.apk"}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Package: ${buildResult.apkPackageName ?: "com.example.app"}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Size: ${buildResult.apkSizeBytes / 1024 / 1024} MB",
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = "CodeAssist uses the Android PackageInstaller API with file provider permissions to install directly onto the device.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentGreen),
                modifier = Modifier.testTag("btn_confirm_launch_apk")
            ) {
                Text("Launch / Install")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}
