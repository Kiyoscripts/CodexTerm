package com.yourpackage.codexterm

import android.content.Context
import com.yourpackage.codexterm.data.local.PreferencesHistoryRepository
import com.yourpackage.codexterm.data.repository.MockCodexRepository
import com.yourpackage.codexterm.data.terminal.TerminalManager
import com.yourpackage.codexterm.domain.usecase.GenerateCodeUseCase
import com.yourpackage.codexterm.domain.usecase.ObserveHistoryUseCase
import com.yourpackage.codexterm.domain.usecase.SaveHistoryEntryUseCase

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences("codexterm_prefs", Context.MODE_PRIVATE)
    private val historyRepository = PreferencesHistoryRepository(preferences)
    private val codexRepository = MockCodexRepository()

    val generateCodeUseCase = GenerateCodeUseCase(codexRepository)
    val observeHistoryUseCase = ObserveHistoryUseCase(historyRepository)
    val saveHistoryEntryUseCase = SaveHistoryEntryUseCase(historyRepository)
    val terminalManager = TerminalManager(appContext)
}
