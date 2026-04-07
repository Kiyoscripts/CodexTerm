package com.yourpackage.codexterm.domain.model

data class PromptHistoryEntry(
    val id: Long,
    val prompt: String,
    val language: String,
    val response: String,
    val timestamp: Long,
)
