package com.example.codeassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeassist.data.CodeCompletionEngine
import com.example.codeassist.data.CompletionItem
import com.example.codeassist.data.SyntaxHighlighter
import com.example.codeassist.model.CodeFile
import com.example.codeassist.ui.theme.*

@Composable
fun CodeEditorView(
    openFiles: List<CodeFile>,
    activeFile: CodeFile?,
    fontSizeSp: Int,
    showLineNumbers: Boolean,
    onSelectFile: (CodeFile) -> Unit,
    onCloseFile: (CodeFile) -> Unit,
    onContentChange: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeFile == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(EditorBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No open files",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Select a file from the explorer to start editing",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    var textValue by remember(activeFile.id) {
        mutableStateOf(TextFieldValue(activeFile.content))
    }

    // Sync if content changed externally (e.g. undo/redo or block editor sync)
    LaunchedEffect(activeFile.content) {
        if (textValue.text != activeFile.content) {
            textValue = textValue.copy(text = activeFile.content)
        }
    }

    // Autocomplete query detection
    var showCompletions by remember { mutableStateOf(false) }
    var completionQuery by remember { mutableStateOf("") }
    val completions = remember(completionQuery, activeFile.fileType) {
        CodeCompletionEngine.getSuggestions(completionQuery, activeFile.fileType)
    }

    // Find in file state
    var showFindBar by remember { mutableStateOf(false) }
    var findText by remember { mutableStateOf("") }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EditorBackground)
            .testTag("code_editor_view")
    ) {
        // Tab Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(IdeSurfaceDark)
                .border(androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark))
                .testTag("editor_tab_bar")
        ) {
            items(openFiles, key = { it.id }) { file ->
                val isActive = file.id == activeFile.id

                Row(
                    modifier = Modifier
                        .background(if (isActive) EditorBackground else IdeSurfaceDark)
                        .clickable { onSelectFile(file) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .border(
                            width = if (isActive) 2.dp else 0.dp,
                            color = if (isActive) IdePrimary else Color.Transparent,
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FileIcon(fileType = file.fileType)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = file.name,
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (file.isDirty) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(IdeAccentAmber)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onCloseFile(file) },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Tab",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // Optional Find in File Bar
        if (showFindBar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IdeSurfaceVariantDark)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = findText,
                    onValueChange = { findText = it },
                    placeholder = { Text("Find…", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("input_find_text"),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = EditorBackground,
                        unfocusedContainerColor = EditorBackground
                    )
                )
                IconButton(onClick = { showFindBar = false }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close Find", modifier = Modifier.size(16.dp))
                }
            }
        }

        // Code Editor Body with Line Numbers
        val lines = remember(textValue.text) {
            textValue.text.split("\n")
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                // Line Numbers Gutter
                if (showLineNumbers) {
                    Column(
                        modifier = Modifier
                            .background(IdeSurfaceDark)
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        for (i in 1..lines.size) {
                            Text(
                                text = i.toString(),
                                fontSize = fontSizeSp.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EditorLineNumber,
                                lineHeight = (fontSizeSp * 1.5).sp
                            )
                        }
                    }
                }

                // Text Editor Surface
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    // Syntax Highlighted underlying view (Annotated string overlay)
                    val highlighted = remember(textValue.text, activeFile.fileType) {
                        SyntaxHighlighter.highlight(textValue.text, activeFile.fileType)
                    }

                    // Editable TextField
                    BasicTextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            textValue = newValue
                            onContentChange(newValue.text)

                            // Detect last word for completions
                            val cursor = newValue.selection.end
                            if (cursor > 0 && cursor <= newValue.text.length) {
                                val beforeCursor = newValue.text.substring(0, cursor)
                                val lastWord = beforeCursor.takeLastWhile { it.isLetterOrDigit() || it == '.' }
                                if (lastWord.length >= 2) {
                                    completionQuery = lastWord
                                    showCompletions = true
                                } else {
                                    showCompletions = false
                                }
                            } else {
                                showCompletions = false
                            }
                        },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = fontSizeSp.sp,
                            color = SyntaxDefault,
                            lineHeight = (fontSizeSp * 1.5).sp
                        ),
                        cursorBrush = SolidColor(IdePrimary),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("code_editor_text_input")
                    )
                }
            }

            // Floating Autocomplete Suggestion Popup
            if (showCompletions && completions.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 64.dp, bottom = 48.dp)
                        .widthIn(min = 220.dp, max = 320.dp)
                        .border(1.dp, IdeBorderDark, RoundedCornerShape(8.dp))
                        .testTag("autocomplete_popup"),
                    shape = RoundedCornerShape(8.dp),
                    color = IdeSurfaceDark,
                    tonalElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Suggestions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IdePrimary)
                            IconButton(onClick = { showCompletions = false }, modifier = Modifier.size(16.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                            }
                        }
                        completions.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        // Insert completion
                                        val cursor = textValue.selection.end
                                        val beforeCursor = textValue.text.substring(0, cursor)
                                        val afterCursor = textValue.text.substring(cursor)
                                        val startWord = beforeCursor.lastIndexOfAny(charArrayOf(' ', '\n', '\t', '.', '(', ')')).let { if (it == -1) 0 else it + 1 }
                                        val newText = textValue.text.substring(0, startWord) + item.insertText + afterCursor
                                        textValue = TextFieldValue(newText, selection = androidx.compose.ui.text.TextRange(startWord + item.insertText.length))
                                        onContentChange(newText)
                                        showCompletions = false
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.label,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SyntaxKeyword
                                    )
                                    Text(
                                        text = item.detail,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(IdeSurfaceVariantDark)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(item.kind, fontSize = 9.sp, color = IdePrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Symbol Keyboard Bar (CodeAssist signature on-screen symbol helper)
        SymbolKeyboardBar(
            onInsertSymbol = { sym ->
                val cursor = textValue.selection.end
                val before = textValue.text.substring(0, cursor)
                val after = textValue.text.substring(cursor)
                val newText = before + sym + after
                textValue = TextFieldValue(newText, selection = androidx.compose.ui.text.TextRange(cursor + sym.length))
                onContentChange(newText)
            },
            onUndo = onUndo,
            onRedo = onRedo,
            onToggleFind = { showFindBar = !showFindBar }
        )
    }
}

@Composable
private fun SymbolKeyboardBar(
    onInsertSymbol: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onToggleFind: () -> Unit
) {
    val symbols = listOf(
        "    ", "{", "}", "(", ")", ";", "=", "\"", "'", ".", "->", "[", "]", ":", "<", ">", ",", "!", "val ", "fun ", "if "
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("symbol_keyboard_bar"),
        color = IdeSurfaceDark,
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, IdeBorderDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo & Redo buttons
            IconButton(onClick = onUndo, modifier = Modifier.size(32.dp).testTag("btn_undo")) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onRedo, modifier = Modifier.size(32.dp).testTag("btn_redo")) {
                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onToggleFind, modifier = Modifier.size(32.dp).testTag("btn_find_in_file")) {
                Icon(Icons.Default.Search, contentDescription = "Find", modifier = Modifier.size(18.dp))
            }

            VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 2.dp))

            // Scrollable symbols
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(symbols) { sym ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(IdeSurfaceVariantDark)
                            .clickable { onInsertSymbol(sym) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("symbol_${sym.trim().ifEmpty { "tab" }}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (sym == "    ") "TAB" else sym,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
