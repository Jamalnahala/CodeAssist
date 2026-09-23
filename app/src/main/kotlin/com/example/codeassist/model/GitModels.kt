package com.example.codeassist.model

enum class GitFileStatus {
    MODIFIED,
    ADDED,
    DELETED,
    UNTRACKED
}

data class GitChangedFile(
    val path: String,
    val status: GitFileStatus,
    val isStaged: Boolean = false,
    val diffAdditions: Int = 0,
    val diffDeletions: Int = 0
)

data class GitCommit(
    val hash: String,
    val message: String,
    val author: String,
    val timestamp: String,
    val filesChangedCount: Int
)

data class GitBranch(
    val name: String,
    val isCurrent: Boolean = false
)
