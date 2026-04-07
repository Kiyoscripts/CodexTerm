package com.yourpackage.codexterm.domain.usecase

import com.yourpackage.codexterm.domain.model.PromptHistoryEntry
import com.yourpackage.codexterm.domain.repository.HistoryRepository

class SaveHistoryEntryUseCase(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(entry: PromptHistoryEntry) {
        historyRepository.saveEntry(entry)
    }
}
