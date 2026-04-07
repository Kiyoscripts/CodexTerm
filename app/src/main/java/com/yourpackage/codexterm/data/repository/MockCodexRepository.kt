package com.yourpackage.codexterm.data.repository

import com.yourpackage.codexterm.domain.repository.CodexRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockCodexRepository : CodexRepository {
    override fun generateCode(prompt: String, language: String): Flow<String> = flow {
        val snippet = buildSnippet(prompt = prompt, language = language)
        val chunks = snippet.chunked(48)
        var runningText = ""
        chunks.forEach { chunk ->
            delay(120)
            runningText += chunk
            emit(runningText)
        }
    }

    private fun buildSnippet(prompt: String, language: String): String {
        return when (language.lowercase()) {
            "kotlin" -> {
                """
                // Mock Codex response for: $prompt
                class GeneratedFeature {
                    fun run(input: String): String {
                        require(input.isNotBlank()) { "Input must not be blank" }
                        return "Processed: ${'$'}input"
                    }
                }
                """.trimIndent()
            }

            "python" -> {
                """
                # Mock Codex response for: $prompt
                def run(input_value: str) -> str:
                    if not input_value.strip():
                        raise ValueError("input_value must not be blank")
                    return f"Processed: {input_value}"
                """.trimIndent()
            }

            else -> {
                """
                // Mock Codex response for: $prompt
                function run(input) {
                  if (!input || !input.trim()) throw new Error("input is required");
                  return `Processed: ${'$'}{input}`;
                }
                """.trimIndent()
            }
        }
    }
}
