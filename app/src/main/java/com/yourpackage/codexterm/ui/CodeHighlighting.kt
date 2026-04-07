package com.yourpackage.codexterm.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

private val keywords = setOf(
    "class", "fun", "val", "var", "return", "if", "else", "def", "raise", "function", "throw",
)

fun highlightCode(text: String): AnnotatedString {
    val tokens = text.split(" ")
    return buildAnnotatedString {
        tokens.forEachIndexed { index, token ->
            val cleanToken = token.trim()
            val color = when {
                keywords.contains(cleanToken.replace(Regex("[^A-Za-z]"), "")) -> Color(0xFFFFB86C)
                cleanToken.startsWith("//") || cleanToken.startsWith("#") -> Color(0xFF7EE787)
                cleanToken.startsWith("\"") || cleanToken.startsWith("'") -> Color(0xFF79C0FF)
                else -> Color(0xFFF0E7D8)
            }
            withStyle(SpanStyle(color = color)) {
                append(token)
            }
            if (index != tokens.lastIndex) append(" ")
        }
    }
}
