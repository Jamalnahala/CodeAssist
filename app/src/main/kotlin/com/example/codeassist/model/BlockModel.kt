package com.example.codeassist.model

enum class BlockType(val label: String, val category: String) {
    FUNCTION("Function Definition", "Structure"),
    VARIABLE("Variable Declaration", "Variables"),
    IF_CONDITION("If Statement", "Control"),
    FOR_LOOP("For Loop", "Loops"),
    WHILE_LOOP("While Loop", "Loops"),
    CALL_EXPR("Method Call", "Logic"),
    PRINT_STATEMENT("Print / Log", "Actions"),
    RETURN_STATEMENT("Return Value", "Control"),
    CUSTOM_STATEMENT("Code Expression", "Custom")
}

data class BlockSocket(
    val name: String,
    val currentValue: String,
    val hint: String = "",
    val allowedType: String = "Any"
)

data class AstBlock(
    val id: String,
    val type: BlockType,
    val header: String,
    val sockets: List<BlockSocket> = emptyList(),
    val innerBlocks: List<AstBlock> = emptyList(),
    val colorHex: Long
)
