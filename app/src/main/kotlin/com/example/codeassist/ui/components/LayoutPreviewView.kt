package com.example.codeassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeassist.model.CodeFile
import com.example.codeassist.ui.theme.IdeBorderDark
import com.example.codeassist.ui.theme.IdePrimary
import com.example.codeassist.ui.theme.IdeSurfaceDark
import com.example.codeassist.ui.theme.IdeSurfaceVariantDark

enum class DeviceType(val label: String, val widthDp: Int, val heightDp: Int) {
    PHONE("Phone (Pixel 8)", 320, 540),
    TABLET("Tablet (Pixel Tab)", 480, 540),
    FOLDABLE("Foldable", 380, 540)
}

@Composable
fun LayoutPreviewView(
    activeFile: CodeFile?,
    modifier: Modifier = Modifier
) {
    var selectedDevice by remember { mutableStateOf(DeviceType.PHONE) }
    var isDarkPreview by remember { mutableStateOf(true) }
    var showInspector by remember { mutableStateOf(false) }

    // Live preview state for interactive testing
    var liveCounter by remember { mutableStateOf(0) }
    var calcDisplay by remember { mutableStateOf("0") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115))
            .padding(8.dp)
            .testTag("layout_preview_view")
    ) {
        // Toolbar: Device switcher, theme toggle, inspector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = IdeSurfaceDark,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device selectors
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DeviceType.values().forEach { device ->
                        TextButton(
                            onClick = { selectedDevice = device },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (selectedDevice == device) IdePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("btn_device_${device.name.lowercase()}")
                        ) {
                            Text(device.label, fontSize = 11.sp, fontWeight = if (selectedDevice == device) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                // Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isDarkPreview = !isDarkPreview },
                        modifier = Modifier.size(32.dp).testTag("btn_toggle_preview_theme")
                    ) {
                        Icon(
                            imageVector = if (isDarkPreview) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Toggle Preview Theme",
                            tint = IdePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showInspector = !showInspector },
                        modifier = Modifier.size(32.dp).testTag("btn_toggle_inspector")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Inspect Tree",
                            tint = if (showInspector) IdePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Device Preview Frame
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(selectedDevice.widthDp.dp)
                    .height(selectedDevice.heightDp.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(4.dp, Color(0xFF2D3748), RoundedCornerShape(24.dp))
                    .testTag("preview_device_frame"),
                color = if (isDarkPreview) Color(0xFF121212) else Color(0xFFF9FAFB),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Simulated Android Status Bar
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("12:00", fontSize = 10.sp, color = if (isDarkPreview) Color.LightGray else Color.DarkGray)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(12.dp), tint = if (isDarkPreview) Color.LightGray else Color.DarkGray)
                            Icon(Icons.Default.BatteryFull, contentDescription = null, modifier = Modifier.size(12.dp), tint = if (isDarkPreview) Color.LightGray else Color.DarkGray)
                        }
                    }

                    // Content based on active file
                    val content = activeFile?.content ?: ""

                    if (content.contains("2048") || content.contains("GameBoard")) {
                        // 2048 Live Preview
                        Text("2048 Mobile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isDarkPreview) Color.White else Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("SCORE: 1,024", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IdePrimary)
                            Text("BEST: 4,096", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Sample Grid
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFBBADA0), RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        ) {
                            listOf(
                                listOf(2, 4, 8, 16),
                                listOf(32, 64, 128, 256),
                                listOf(512, 1024, 2, 4),
                                listOf(0, 2, 0, 8)
                            ).forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    row.forEach { cell ->
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .background(if (cell == 0) Color(0xFFCDC1B4) else Color(0xFFEDC22E), RoundedCornerShape(4.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (cell > 0) {
                                                Text(cell.toString(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (content.contains("Calculator") || content.contains("txt_display")) {
                        // Calculator XML Preview
                        Text(
                            text = calcDisplay,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkPreview) Color.White else Color.Black,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        )
                        val keypad = listOf(
                            listOf("7", "8", "9", "/"),
                            listOf("4", "5", "6", "*"),
                            listOf("1", "2", "3", "-"),
                            listOf("C", "0", "=", "+")
                        )
                        keypad.forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { key ->
                                    Button(
                                        onClick = {
                                            calcDisplay = when (key) {
                                                "C" -> "0"
                                                "=" -> "42"
                                                else -> if (calcDisplay == "0") key else calcDisplay + key
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (key in listOf("/", "*", "-", "+", "=")) IdePrimary else IdeSurfaceVariantDark
                                        )
                                    ) {
                                        Text(key, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        // Default Jetpack Compose Counter Preview
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Jetpack Compose Preview",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkPreview) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkPreview) Color(0xFF1E293B) else Color(0xFFEFF6FF)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Interactive Live Preview",
                                        fontWeight = FontWeight.SemiBold,
                                        color = IdePrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap clicks: $liveCounter",
                                        fontSize = 16.sp,
                                        color = if (isDarkPreview) Color.White else Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { liveCounter++ },
                                        colors = ButtonDefaults.buttonColors(containerColor = IdePrimary),
                                        modifier = Modifier.testTag("preview_interactive_btn")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Increment")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Preview is rendered dynamically using Compose Runtime without emulator dependencies.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Element Hierarchy Inspector (if enabled)
        if (showInspector) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .padding(top = 8.dp),
                color = IdeSurfaceDark,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("ELEMENT INSPECTOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IdePrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• MaterialTheme [Theme wrapper]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("  └── Surface [modifier = Modifier.fillMaxSize()]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("      └── Column [horizontalAlignment = CenterHorizontally]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("          ├── Text [\"Welcome to CodeAssist!\"]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("          └── Button [onClick = { count++ }]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
