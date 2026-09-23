package com.example.codeassist.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.codeassist.model.FileType
import com.example.codeassist.ui.theme.*

object SyntaxHighlighter {

    private val KOTLIN_KEYWORDS = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if",
        "in", "interface", "is", "null", "object", "package", "return", "super", "this",
        "throw", "true", "try", "typealias", "val", "var", "when", "while", "by", "companion",
        "constructor", "init", "override", "private", "protected", "public", "internal",
        "data", "sealed", "open", "abstract", "suspend", "import", "inline", "crossinline", "reified"
    )

    private val JAVA_KEYWORDS = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
        "finally", "float", "for", "if", "implements", "import", "instanceof", "int", "interface",
        "long", "native", "new", "package", "private", "protected", "public", "return", "short",
        "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while", "true", "false", "null", "record"
    )

    private val COMMON_TYPES = setOf(
        "String", "Int", "Boolean", "Long", "Double", "Float", "List", "Map", "Set",
        "ArrayList", "HashMap", "Unit", "Any", "Modifier", "Composable", "State", "Color",
        "Context", "Intent", "Bundle", "Activity", "View", "TextView", "Button", "Card"
    )

    fun highlight(text: String, fileType: FileType): AnnotatedString {
        if (text.isEmpty()) return AnnotatedString("")

        return buildAnnotatedString {
            append(text)

            when (fileType) {
                FileType.KOTLIN, FileType.JAVA, FileType.GRADLE -> highlightCode(text, fileType)
                FileType.XML -> highlightXml(text)
                FileType.JSON -> highlightJson(text)
                FileType.MARKDOWN -> highlightMarkdown(text)
                else -> {
                    addStyle(SpanStyle(color = SyntaxDefault, fontFamily = FontFamily.Monospace), 0, text.length)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.highlightCode(text: String, fileType: FileType) {
        val keywords = if (fileType == FileType.JAVA) JAVA_KEYWORDS else KOTLIN_KEYWORDS

        // Base styling
        addStyle(SpanStyle(color = SyntaxDefault, fontFamily = FontFamily.Monospace), 0, text.length)

        // Strings
        val stringRegex = Regex("\"(\\\\.|[^\"])*\"|'(\\\\.|[^'])*'")
        for (match in stringRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxString), match.range.first, match.range.last + 1)
        }

        // Numbers
        val numberRegex = Regex("\\b\\d+(\\.\\d+)?[fFL]?\\b")
        for (match in numberRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxNumber), match.range.first, match.range.last + 1)
        }

        // Annotations
        val annotationRegex = Regex("@[A-Za-z0-9_]+")
        for (match in annotationRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxAnnotation, fontWeight = FontWeight.SemiBold), match.range.first, match.range.last + 1)
        }

        // Words (Keywords, Types, Functions)
        val wordRegex = Regex("\\b[A-Za-z_][A-Za-z0-9_]*\\b")
        for (match in wordRegex.findAll(text)) {
            val word = match.value
            val start = match.range.first
            val end = match.range.last + 1

            if (keywords.contains(word)) {
                addStyle(SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.Bold), start, end)
            } else if (COMMON_TYPES.contains(word) || (word.isNotEmpty() && word[0].isUpperCase())) {
                addStyle(SpanStyle(color = SyntaxType), start, end)
            }
        }

        // Function declarations
        val funRegex = Regex("(?:fun|void|public|private)\\s+([a-zA-Z0-9_]+)\\s*\\(")
        for (match in funRegex.findAll(text)) {
            val group = match.groups[1]
            if (group != null) {
                addStyle(SpanStyle(color = SyntaxFunction, fontWeight = FontWeight.SemiBold), group.range.first, group.range.last + 1)
            }
        }

        // Single line comments
        val commentRegex = Regex("//.*")
        for (match in commentRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxComment), match.range.first, match.range.last + 1)
        }

        // Multi line comments
        val multiLineCommentRegex = Regex("/\\*[\\s\\S]*?\\*/")
        for (match in multiLineCommentRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxComment), match.range.first, match.range.last + 1)
        }
    }

    private fun AnnotatedString.Builder.highlightXml(text: String) {
        addStyle(SpanStyle(color = SyntaxDefault, fontFamily = FontFamily.Monospace), 0, text.length)

        // Strings / Attributes values
        val stringRegex = Regex("\"([^\"]*)\"")
        for (match in stringRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxString), match.range.first, match.range.last + 1)
        }

        // Tags
        val tagRegex = Regex("</?([A-Za-z0-9_.:]+)")
        for (match in tagRegex.findAll(text)) {
            val group = match.groups[1]
            if (group != null) {
                addStyle(SpanStyle(color = SyntaxTag, fontWeight = FontWeight.SemiBold), group.range.first, group.range.last + 1)
            }
        }

        // Attribute names
        val attrRegex = Regex("\\s([A-Za-z0-9_.:]+)=")
        for (match in attrRegex.findAll(text)) {
            val group = match.groups[1]
            if (group != null) {
                addStyle(SpanStyle(color = SyntaxAttribute), group.range.first, group.range.last + 1)
            }
        }

        // Comments
        val commentRegex = Regex("<!--[\\s\\S]*?-->")
        for (match in commentRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxComment), match.range.first, match.range.last + 1)
        }
    }

    private fun AnnotatedString.Builder.highlightJson(text: String) {
        addStyle(SpanStyle(color = SyntaxDefault, fontFamily = FontFamily.Monospace), 0, text.length)

        val keyRegex = Regex("\"([^\"]+)\"\\s*:")
        for (match in keyRegex.findAll(text)) {
            val group = match.groups[1]
            if (group != null) {
                addStyle(SpanStyle(color = SyntaxAttribute, fontWeight = FontWeight.Bold), group.range.first, group.range.last + 1)
            }
        }

        val stringRegex = Regex(":\\s*\"([^\"]*)\"")
        for (match in stringRegex.findAll(text)) {
            val group = match.groups[1]
            if (group != null) {
                addStyle(SpanStyle(color = SyntaxString), group.range.first, group.range.last + 1)
            }
        }

        val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
        for (match in numberRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxNumber), match.range.first, match.range.last + 1)
        }

        val boolRegex = Regex("\\b(true|false|null)\\b")
        for (match in boolRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxKeyword), match.range.first, match.range.last + 1)
        }
    }

    private fun AnnotatedString.Builder.highlightMarkdown(text: String) {
        addStyle(SpanStyle(color = SyntaxDefault, fontFamily = FontFamily.Monospace), 0, text.length)

        // Headers
        val headerRegex = Regex("^#{1,6}\\s.*$", RegexOption.MULTILINE)
        for (match in headerRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxTag, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
        }

        // Code blocks
        val codeRegex = Regex("`[^`]+`")
        for (match in codeRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxFunction, background = Color(0x3356B6C2)), match.range.first, match.range.last + 1)
        }

        // Links
        val linkRegex = Regex("\\[([^\\]]+)\\]\\(([^)]+)\\)")
        for (match in linkRegex.findAll(text)) {
            addStyle(SpanStyle(color = SyntaxType), match.range.first, match.range.last + 1)
        }
    }
}
