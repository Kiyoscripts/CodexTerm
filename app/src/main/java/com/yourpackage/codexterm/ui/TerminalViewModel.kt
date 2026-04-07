package com.yourpackage.codexterm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourpackage.codexterm.data.terminal.TerminalManager
import com.yourpackage.codexterm.domain.model.TerminalBackend
import com.yourpackage.codexterm.domain.model.TerminalLine
import com.yourpackage.codexterm.domain.model.TerminalStream
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class TerminalUiState(
    val command: String = "pwd",
    val lines: List<TerminalLine> = emptyList(),
    val activeBackend: TerminalBackend = TerminalBackend.IN_APP_SHELL,
    val isRunning: Boolean = false,
)

class TerminalViewModel(
    private val terminalManager: TerminalManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TerminalUiState(activeBackend = terminalManager.backend()),
    )
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    private var commandJob: Job? = null

    fun updateCommand(value: String) {
        _uiState.value = _uiState.value.copy(command = value)
    }

    fun loadGeneratedCodeAsCommand(code: String) {
        if (code.isNotBlank()) {
            _uiState.value = _uiState.value.copy(command = code)
        }
    }

    fun runCommand() {
        val command = _uiState.value.command
        if (command.isBlank()) return

        commandJob?.cancel()
        commandJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRunning = true,
                activeBackend = terminalManager.backend(),
                lines = _uiState.value.lines + TerminalLine(">> $command", TerminalStream.SYSTEM),
            )
            terminalManager.execute(command).catch { throwable ->
                _uiState.value = _uiState.value.copy(
                    lines = _uiState.value.lines + TerminalLine(
                        throwable.message ?: "Command failed",
                        TerminalStream.STDERR,
                    ),
                )
            }.collect { line ->
                _uiState.value = _uiState.value.copy(lines = _uiState.value.lines + line)
            }
            _uiState.value = _uiState.value.copy(isRunning = false)
        }
    }

    fun clearOutput() {
        _uiState.value = _uiState.value.copy(lines = emptyList())
    }

    companion object {
        fun factory(terminalManager: TerminalManager): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TerminalViewModel(terminalManager) as T
                }
            }
        }
    }
}
