package com.yourpackage.codexterm.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.yourpackage.codexterm.domain.model.TerminalBackend
import com.yourpackage.codexterm.domain.model.TerminalLine
import com.yourpackage.codexterm.domain.model.TerminalStream
import com.yourpackage.codexterm.ui.TerminalUiState
import com.yourpackage.codexterm.ui.theme.SurfaceRaised
import com.yourpackage.codexterm.ui.theme.TerminalBlue
import com.yourpackage.codexterm.ui.theme.TerminalGreen
import com.yourpackage.codexterm.ui.theme.TerminalRed

@Composable
fun TerminalScreen(
    state: TerminalUiState,
    onCommandChanged: (String) -> Unit,
    onRunCommand: () -> Unit,
    onClearOutput: () -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.lines.size) {
        if (state.lines.isNotEmpty()) {
            listState.animateScrollToItem(state.lines.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(shape = RoundedCornerShape(20.dp), color = SurfaceRaised) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Terminal", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Backend: ${backendLabel(state.activeBackend)}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
                BasicTextField(
                    value = state.command,
                    onValueChange = onCommandChanged,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (state.command.isBlank()) {
                                Text(
                                    "echo \"hello\"",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ElevatedButton(onClick = onRunCommand, enabled = !state.isRunning) {
                        Text(if (state.isRunning) "Running..." else "Run")
                    }
                    OutlinedButton(onClick = onClearOutput) {
                        Text("Clear")
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceRaised,
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.lines, key = { "${it.timestamp}-${it.text.hashCode()}" }) { line ->
                    TerminalLineItem(line)
                }
            }
        }
    }
}

@Composable
private fun TerminalLineItem(line: TerminalLine) {
    val color = when (line.stream) {
        TerminalStream.STDOUT -> TerminalGreen
        TerminalStream.STDERR -> TerminalRed
        TerminalStream.SYSTEM -> TerminalBlue
    }
    Text(
        text = line.text,
        color = color,
        fontFamily = FontFamily.Monospace,
        style = MaterialTheme.typography.bodyMedium,
    )
}

private fun backendLabel(backend: TerminalBackend): String {
    return when (backend) {
        TerminalBackend.TERMUX_API -> "Termux:API"
        TerminalBackend.IN_APP_SHELL -> "In-app shell"
    }
}
