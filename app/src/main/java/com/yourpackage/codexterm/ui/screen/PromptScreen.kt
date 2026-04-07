package com.yourpackage.codexterm.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.yourpackage.codexterm.ui.PromptUiState
import com.yourpackage.codexterm.ui.highlightCode
import com.yourpackage.codexterm.ui.theme.SurfaceRaised

private val languages = listOf("Kotlin", "Python", "JavaScript")

@Composable
fun PromptScreen(
    state: PromptUiState,
    onPromptChanged: (String) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onGenerate: () -> Unit,
    onGeneratedCodeChanged: (String) -> Unit,
    onSendToTerminal: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Prompt Codex", style = MaterialTheme.typography.headlineSmall)
        Surface(shape = RoundedCornerShape(20.dp), color = SurfaceRaised) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Describe the code you want to generate")
                BasicTextField(
                    value = state.prompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    decorationBox = { innerTextField ->
                        Box {
                            if (state.prompt.isEmpty()) {
                                Text(
                                    "Build a Kotlin CLI tool that lists files and filters by extension",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                )
                            }
                            innerTextField()
                        }
                    },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text(state.selectedLanguage)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            languages.forEach { language ->
                                DropdownMenuItem(
                                    text = { Text(language) },
                                    onClick = {
                                        onLanguageSelected(language)
                                        expanded = false
                                    },
                                )
                            }
                        }
                    }
                    ElevatedButton(onClick = onGenerate, enabled = !state.isLoading) {
                        if (state.isLoading) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                        } else {
                            Text("Generate")
                        }
                    }
                    OutlinedButton(onClick = onSendToTerminal, enabled = state.generatedCode.isNotBlank()) {
                        Text("Run in Terminal")
                    }
                }
                state.errorMessage?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceRaised,
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Generated Code", style = MaterialTheme.typography.titleMedium)
                BasicTextField(
                    value = state.generatedCode,
                    onValueChange = onGeneratedCodeChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .horizontalScroll(verticalScroll),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace,
                    ),
                    decorationBox = { innerTextField ->
                        if (state.generatedCode.isBlank()) {
                            Text(
                                "Generated code will stream here.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        innerTextField()
                    },
                )
                if (state.generatedCode.isNotBlank()) {
                    Text(
                        text = highlightCode(state.generatedCode),
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    )
                }
            }
        }
    }
}
