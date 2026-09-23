package com.example.codeassist.data

import com.example.codeassist.model.FileType

data class CompletionItem(
    val label: String,
    val detail: String,
    val insertText: String,
    val kind: String = "Keyword",
    val score: Int = 100
)

object CodeCompletionEngine {

    private val KOTLIN_COMPLETIONS = listOf(
        CompletionItem("fun", "fun name(): Unit", "fun name() {\n    \n}", "Keyword", 100),
        CompletionItem("val", "Read-only property", "val propertyName = ", "Keyword", 98),
        CompletionItem("var", "Mutable property", "var propertyName = ", "Keyword", 97),
        CompletionItem("class", "Class declaration", "class Name {\n    \n}", "Keyword", 95),
        CompletionItem("data class", "Data class declaration", "data class Name(val id: String)", "Keyword", 95),
        CompletionItem("Composable", "Compose UI function annotation", "@Composable\nfun MyComponent() {\n    \n}", "Annotation", 94),
        CompletionItem("remember", "Remember value across recomposition", "remember { mutableStateOf() }", "Compose", 93),
        CompletionItem("mutableStateOf", "Create observable mutable state", "mutableStateOf(\"\")", "Compose", 92),
        CompletionItem("LaunchedEffect", "Run suspend effect in Composable", "LaunchedEffect(Unit) {\n    \n}", "Compose", 91),
        CompletionItem("Modifier", "Compose Layout Modifier", "Modifier.fillMaxWidth().padding(16.dp)", "Compose", 90),
        CompletionItem("Column", "Vertical stack layout", "Column(\n    modifier = Modifier.fillMaxWidth()\n) {\n    \n}", "Layout", 89),
        CompletionItem("Row", "Horizontal stack layout", "Row(\n    modifier = Modifier.fillMaxWidth()\n) {\n    \n}", "Layout", 88),
        CompletionItem("Text", "Display text component", "Text(text = \"Hello World\")", "Component", 87),
        CompletionItem("Button", "Material 3 Button", "Button(onClick = { /* action */ }) {\n    Text(\"Click Me\")\n}", "Component", 86),
        CompletionItem("Card", "Material 3 Card", "Card(modifier = Modifier.fillMaxWidth()) {\n    \n}", "Component", 85),
        CompletionItem("LazyColumn", "Vertically scrolling list", "LazyColumn {\n    items(itemList) { item ->\n        \n    }\n}", "Layout", 84),
        CompletionItem("println", "Print to stdout", "println(\"\")", "Function", 80),
        CompletionItem("if", "Conditional branch", "if (condition) {\n    \n} else {\n    \n}", "Keyword", 78),
        CompletionItem("when", "Pattern matching expression", "when (state) {\n    is Success -> { }\n    is Error -> { }\n}", "Keyword", 77)
    )

    private val XML_COMPLETIONS = listOf(
        CompletionItem("LinearLayout", "Linear Layout container", "<LinearLayout\n    xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"match_parent\"\n    android:orientation=\"vertical\">\n    \n</LinearLayout>", "Tag", 100),
        CompletionItem("TextView", "Text View element", "<TextView\n    android:layout_width=\"wrap_content\"\n    android:layout_height=\"wrap_content\"\n    android:text=\"Hello World\" />", "Tag", 95),
        CompletionItem("Button", "Button element", "<Button\n    android:id=\"@+id/btn_action\"\n    android:layout_width=\"wrap_content\"\n    android:layout_height=\"wrap_content\"\n    android:text=\"Submit\" />", "Tag", 90),
        CompletionItem("ImageView", "Image element", "<ImageView\n    android:layout_width=\"wrap_content\"\n    android:layout_height=\"wrap_content\"\n    android:contentDescription=\"@string/app_name\" />", "Tag", 85),
        CompletionItem("android:layout_width", "Width attribute", "android:layout_width=\"match_parent\"", "Attribute", 80),
        CompletionItem("android:layout_height", "Height attribute", "android:layout_height=\"wrap_content\"", "Attribute", 79),
        CompletionItem("android:id", "Resource ID attribute", "android:id=\"@+id/\"", "Attribute", 78),
        CompletionItem("android:padding", "Padding attribute", "android:padding=\"16dp\"", "Attribute", 77)
    )

    fun getSuggestions(query: String, fileType: FileType): List<CompletionItem> {
        val pool = when (fileType) {
            FileType.XML -> XML_COMPLETIONS
            else -> KOTLIN_COMPLETIONS
        }

        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return pool.take(6)

        return pool
            .filter { it.label.lowercase().contains(trimmed) || it.detail.lowercase().contains(trimmed) }
            .sortedByDescending { it.score }
            .take(6)
    }
}
