package com.yourpackage.codexterm.domain.model

data class TerminalSession(
    val activeBackend: TerminalBackend,
    val lines: List<TerminalLine> = emptyList(),
    val workingDirectory: String,
    val isRunning: Boolean = false,
)
