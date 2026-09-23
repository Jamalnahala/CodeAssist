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
import com.example.codeassist.model.AstBlock
import com.example.codeassist.model.BlockSocket
import com.example.codeassist.model.BlockType
import com.example.codeassist.model.CodeFile
import com.example.codeassist.ui.theme.*

@Composable
fun BlockEditorView(
    activeFile: CodeFile?,
    onSyncCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeFile == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active file selected for Block Editing")
        }
        return
    }

    // Parse simple blocks from code file for interactive projection
    var blocks by remember(activeFile.id) {
        mutableStateOf(parseCodeToBlocks(activeFile.content))
    }

    var showAddBlockModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EditorBackground)
            .padding(12.dp)
            .testTag("block_editor_view")
    ) {
        // Block Editor Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = null,
                        tint = IdePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VISUAL BLOCK PROJECTION",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Live bi-directional projection of ${activeFile.name}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                Button(
                    onClick = { showAddBlockModal = true },
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_add_block"),
                    colors = ButtonDefaults.buttonColors(containerColor = IdeSurfaceVariantDark),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Block", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val generatedCode = blocksToCode(blocks, activeFile.content)
                        onSyncCode(generatedCode)
                    },
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_sync_blocks_to_code"),
                    colors = ButtonDefaults.buttonColors(containerColor = IdePrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply to Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Blocks Tree Container
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(blocks, key = { it.id }) { block ->
                BlockCard(
                    block = block,
                    onUpdateSocket = { socketIdx, newVal ->
                        blocks = blocks.map { b ->
                            if (b.id == block.id) {
                                val updatedSockets = b.sockets.mapIndexed { idx, sock ->
                                    if (idx == socketIdx) sock.copy(currentValue = newVal) else sock
                                }
                                b.copy(sockets = updatedSockets)
                            } else b
                        }
                    },
                    onDeleteBlock = {
                        blocks = blocks.filter { it.id != block.id }
                    }
                )
            }
        }
    }

    if (showAddBlockModal) {
        AddBlockDialog(
            onDismiss = { showAddBlockModal = false },
            onAdd = { newBlock ->
                blocks = blocks + newBlock
                showAddBlockModal = false
            }
        )
    }
}

@Composable
fun BlockCard(
    block: AstBlock,
    onUpdateSocket: (Int, String) -> Unit,
    onDeleteBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val blockColor = Color(block.colorHex)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, blockColor.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
            .testTag("block_card_${block.id}"),
        color = IdeSurfaceDark,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Puzzle top header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(blockColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = block.type.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = blockColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = block.header,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDeleteBlock,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Block",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sockets (Editable puzzle pieces)
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                block.sockets.forEachIndexed { idx, socket ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = socket.name + ":",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(80.dp)
                        )

                        OutlinedTextField(
                            value = socket.currentValue,
                            onValueChange = { onUpdateSocket(idx, it) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("socket_input_${block.id}_$idx"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = blockColor,
                                unfocusedBorderColor = IdeBorderDark,
                                focusedContainerColor = IdeSurfaceVariantDark,
                                unfocusedContainerColor = IdeSurfaceVariantDark
                            )
                        )
                    }
                }
            }

            // Inner nested blocks if any
            if (block.innerBlocks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .border(1.dp, IdeBorderDark, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    block.innerBlocks.forEach { inner ->
                        Text(
                            text = "• ${inner.header}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddBlockDialog(
    onDismiss: () -> Unit,
    onAdd: (AstBlock) -> Unit
) {
    val options = listOf(
        AstBlock(
            id = "b_${System.currentTimeMillis()}_1",
            type = BlockType.VARIABLE,
            header = "Define Variable",
            sockets = listOf(
                BlockSocket("Name", "newCount", allowedType = "String"),
                BlockSocket("Value", "0", allowedType = "Int")
            ),
            colorHex = 0xFF10B981
        ),
        AstBlock(
            id = "b_${System.currentTimeMillis()}_2",
            type = BlockType.IF_CONDITION,
            header = "If Condition",
            sockets = listOf(
                BlockSocket("Condition", "count > 10", allowedType = "Boolean")
            ),
            colorHex = 0xFFF59E0B
        ),
        AstBlock(
            id = "b_${System.currentTimeMillis()}_3",
            type = BlockType.PRINT_STATEMENT,
            header = "Log Statement",
            sockets = listOf(
                BlockSocket("Message", "\"Counter updated!\"", allowedType = "String")
            ),
            colorHex = 0xFF06B6D4
        ),
        AstBlock(
            id = "b_${System.currentTimeMillis()}_4",
            type = BlockType.FUNCTION,
            header = "Composable Screen",
            sockets = listOf(
                BlockSocket("Name", "MyScreen()", allowedType = "Function")
            ),
            colorHex = 0xFF8B5CF6
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Visual Block") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { opt ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAdd(opt) },
                        shape = RoundedCornerShape(8.dp),
                        color = IdeSurfaceVariantDark
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(opt.colorHex))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(opt.header, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(opt.type.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun parseCodeToBlocks(content: String): List<AstBlock> {
    val list = mutableListOf<AstBlock>()

    // Look for functions
    if (content.contains("fun ")) {
        val funRegex = Regex("fun\\s+([a-zA-Z0-9_]+)\\s*\\(([^)]*)\\)")
        val match = funRegex.find(content)
        val funName = match?.groups?.get(1)?.value ?: "MainFunction"
        list.add(
            AstBlock(
                id = "b_fun",
                type = BlockType.FUNCTION,
                header = "@Composable $funName()",
                sockets = listOf(
                    BlockSocket("Function", funName),
                    BlockSocket("Visibility", "public")
                ),
                colorHex = 0xFF8B5CF6
            )
        )
    }

    // Look for variables
    val varRegex = Regex("(val|var)\\s+([a-zA-Z0-9_]+)\\s*=\\s*([^\\n;]+)")
    for ((idx, match) in varRegex.findAll(content).take(3).withIndex()) {
        val kind = match.groups[1]?.value ?: "val"
        val name = match.groups[2]?.value ?: "item"
        val initVal = match.groups[3]?.value ?: "0"
        list.add(
            AstBlock(
                id = "b_var_$idx",
                type = BlockType.VARIABLE,
                header = "$kind $name",
                sockets = listOf(
                    BlockSocket("Name", name),
                    BlockSocket("Value", initVal.trim())
                ),
                colorHex = 0xFF10B981
            )
        )
    }

    // Look for statements
    if (content.contains("Button")) {
        list.add(
            AstBlock(
                id = "b_ui_button",
                type = BlockType.CALL_EXPR,
                header = "Button Component",
                sockets = listOf(
                    BlockSocket("Label", "\"Increment Counter\""),
                    BlockSocket("Action", "count++")
                ),
                colorHex = 0xFF5B8DEF
            )
        )
    }

    if (list.isEmpty()) {
        list.add(
            AstBlock(
                id = "b_default",
                type = BlockType.FUNCTION,
                header = "Function Body",
                sockets = listOf(
                    BlockSocket("Entry", "start()")
                ),
                colorHex = 0xFF8B5CF6
            )
        )
    }

    return list
}

private fun blocksToCode(blocks: List<AstBlock>, originalContent: String): String {
    // Apply block socket edits back into code
    var updated = originalContent
    for (block in blocks) {
        if (block.type == BlockType.VARIABLE) {
            val name = block.sockets.find { it.name == "Name" }?.currentValue
            val value = block.sockets.find { it.name == "Value" }?.currentValue
            if (name != null && value != null) {
                // If it already exists, replace it
                val pattern = Regex("(val|var)\\s+$name\\s*=\\s*[^\\n;]+")
                if (pattern.containsMatchIn(updated)) {
                    updated = pattern.replace(updated, "val $name = $value")
                }
            }
        }
    }
    return updated
}
