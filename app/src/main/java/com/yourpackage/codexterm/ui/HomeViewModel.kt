package com.yourpackage.codexterm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourpackage.codexterm.domain.model.PromptHistoryEntry
import com.yourpackage.codexterm.domain.usecase.ObserveHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val history: List<PromptHistoryEntry> = emptyList(),
)

class HomeViewModel(
    private val observeHistoryUseCase: ObserveHistoryUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeHistoryUseCase().collect { history ->
                _uiState.value = HomeUiState(history = history)
            }
        }
    }

    companion object {
        fun factory(observeHistoryUseCase: ObserveHistoryUseCase): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(observeHistoryUseCase) as T
                }
            }
        }
    }
}
