package com.yourpackage.codexterm.ui.navigation

enum class CodexTermDestination(
    val route: String,
    val label: String,
) {
    Home("home", "Home"),
    Prompt("prompt", "Prompt"),
    Terminal("terminal", "Terminal"),
}
