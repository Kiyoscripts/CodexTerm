package com.yourpackage.codexterm.domain.model

data class TerminalLine(
    val text: String,
    val stream: TerminalStream,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class TerminalStream {
    STDOUT,
    STDERR,
    SYSTEM,
}
