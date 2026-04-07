package com.yourpackage.codexterm.domain.repository

import com.yourpackage.codexterm.domain.model.PromptHistoryEntry
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeHistory(): Flow<List<PromptHistoryEntry>>
    suspend fun saveEntry(entry: PromptHistoryEntry)
}
