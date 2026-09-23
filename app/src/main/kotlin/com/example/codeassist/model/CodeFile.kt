package com.example.codeassist.model

enum class FileType {
    KOTLIN,
    JAVA,
    XML,
    GRADLE,
    JSON,
    MARKDOWN,
    PROPERTIES,
    OTHER
}

data class CodeDiagnostic(
    val line: Int,
    val column: Int = 1,
    val message: String,
    val isError: Boolean = true,
    val quickFix: String? = null
)

data class CodeFile(
    val id: String,
    val name: String,
    val path: String,
    val isDirectory: Boolean = false,
    val content: String = "",
    val children: List<CodeFile> = emptyList(),
    val isDirty: Boolean = false,
    val diagnostics: List<CodeDiagnostic> = emptyList()
) {
    val fileType: FileType
        get() = when {
            name.endsWith(".kt") || name.endsWith(".kts") -> FileType.KOTLIN
            name.endsWith(".java") -> FileType.JAVA
            name.endsWith(".xml") -> FileType.XML
            name.endsWith(".gradle") -> FileType.GRADLE
            name.endsWith(".json") -> FileType.JSON
            name.endsWith(".md") -> FileType.MARKDOWN
            name.endsWith(".properties") -> FileType.PROPERTIES
            else -> FileType.OTHER
        }

    val canVisualPreview: Boolean
        get() = fileType == FileType.XML || (fileType == FileType.KOTLIN && (content.contains("@Composable") || content.contains("@Preview")))

    val canBlockEdit: Boolean
        get() = fileType == FileType.KOTLIN || fileType == FileType.JAVA
}
