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
import com.example.codeassist.model.CodeFile
import com.example.codeassist.ui.theme.*

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val codeSnippet: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantSheet(
    activeFile: CodeFile?,
    onApplyCode: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "m1",
                isUser = false,
                text = "Hello! I am your CodeAssist On-Device AI Copilot. I can analyze '${activeFile?.name ?: "your project"}', generate Jetpack Compose components, or suggest architectural fixes. What would you like to build?"
            )
        )
    }

    val quickPrompts = listOf(
        "Explain this file",
        "Add an animated button",
        "Refactor to Compose M3",
        "Generate Unit Tests"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = IdeSurfaceDark,
        tonalElevation = 10.dp,
        modifier = modifier.testTag("ai_assistant_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = IdePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CODEASSIST AI ASSISTANT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickPrompts.take(3).forEach { chip ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(IdeSurfaceVariantDark)
                            .clickable {
                                promptInput = chip
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(chip, fontSize = 11.sp, color = IdePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat Message List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (msg.isUser) IdePrimary else IdeSurfaceVariantDark,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(10.dp),
                                fontSize = 12.sp,
                                color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (msg.codeSnippet != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EditorBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark),
                                modifier = Modifier.widthIn(max = 300.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = msg.codeSnippet,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = SyntaxFunction
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { onApplyCode(msg.codeSnippet) },
                                        modifier = Modifier.height(28.dp).align(Alignment.End).testTag("btn_apply_ai_code"),
                                        colors = ButtonDefaults.buttonColors(containerColor = IdeAccentGreen),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Apply to Editor", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("Ask Copilot or request code…", fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("input_ai_prompt"),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = IdeSurfaceVariantDark,
                        unfocusedContainerColor = IdeSurfaceVariantDark
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val query = promptInput.trim()
                        if (query.isNotEmpty()) {
                            messages.add(ChatMessage(id = "user_${System.currentTimeMillis()}", isUser = true, text = query))
                            promptInput = ""

                            // Generate helpful response
                            val snippet = if (query.contains("button", ignoreCase = true) || query.contains("compose", ignoreCase = true)) {
                                """@Composable
fun FancyButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.Star, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Enhanced Action")
    }
}"""
                            } else null

                            messages.add(
                                ChatMessage(
                                    id = "ai_${System.currentTimeMillis()}",
                                    isUser = false,
                                    text = "Here is the code suggestion for '${activeFile?.name ?: "your file"}'.",
                                    codeSnippet = snippet
                                )
                            )
                        }
                    },
                    modifier = Modifier.size(44.dp).testTag("btn_send_ai_prompt")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = IdePrimary
                    )
                }
            }
        }
    }
}
