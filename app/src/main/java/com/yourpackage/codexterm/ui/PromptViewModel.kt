package com.yourpackage.codexterm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourpackage.codexterm.domain.model.CodeGenerationRequest
import com.yourpackage.codexterm.domain.model.PromptHistoryEntry
import com.yourpackage.codexterm.domain.usecase.GenerateCodeUseCase
import com.yourpackage.codexterm.domain.usecase.SaveHistoryEntryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class PromptUiState(
    val prompt: String = "",
    val selectedLanguage: String = "Kotlin",
    val generatedCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class PromptViewModel(
    private val generateCodeUseCase: GenerateCodeUseCase,
    private val saveHistoryEntryUseCase: SaveHistoryEntryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptUiState())
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    private var generateJob: Job? = null

    fun updatePrompt(value: String) {
        _uiState.value = _uiState.value.copy(prompt = value)
    }

    fun updateLanguage(language: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }

    fun updateGeneratedCode(code: String) {
        _uiState.value = _uiState.value.copy(generatedCode = code)
    }

    fun generate() {
        val current = _uiState.value
        if (current.prompt.isBlank()) {
            _uiState.value = current.copy(errorMessage = "Prompt cannot be empty")
            return
        }

        generateJob?.cancel()
        generateJob = viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, errorMessage = null, generatedCode = "")
            generateCodeUseCase(
                CodeGenerationRequest(
                    prompt = current.prompt,
                    language = current.selectedLanguage,
                ),
            ).catch { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Generation failed",
                )
            }.collect { partial ->
                _uiState.value = _uiState.value.copy(generatedCode = partial)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
            saveHistoryEntryUseCase(
                PromptHistoryEntry(
                    id = System.currentTimeMillis(),
                    prompt = current.prompt,
                    language = current.selectedLanguage,
                    response = _uiState.value.generatedCode,
                    timestamp = System.currentTimeMillis(),
                ),
            )
        }
    }

    companion object {
        fun factory(
            generateCodeUseCase: GenerateCodeUseCase,
            saveHistoryEntryUseCase: SaveHistoryEntryUseCase,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PromptViewModel(generateCodeUseCase, saveHistoryEntryUseCase) as T
                }
            }
        }
    }
}
