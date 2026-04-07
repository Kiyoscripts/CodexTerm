package com.yourpackage.codexterm.data.local

import android.content.SharedPreferences
import com.yourpackage.codexterm.data.model.HistoryJson
import com.yourpackage.codexterm.domain.model.PromptHistoryEntry
import com.yourpackage.codexterm.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PreferencesHistoryRepository(
    private val sharedPreferences: SharedPreferences,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : HistoryRepository {

    private val historyKey = "prompt_history"
    private val historyState = MutableStateFlow(loadEntries())

    override fun observeHistory(): Flow<List<PromptHistoryEntry>> = historyState.asStateFlow()

    override suspend fun saveEntry(entry: PromptHistoryEntry) {
        withContext(ioDispatcher) {
            val updated = listOf(entry) + historyState.value
            val trimmed = updated.take(50)
            sharedPreferences.edit().putString(historyKey, HistoryJson.encode(trimmed)).apply()
            historyState.value = trimmed
        }
    }

    private fun loadEntries(): List<PromptHistoryEntry> {
        return HistoryJson.decode(sharedPreferences.getString(historyKey, null))
    }
}
