package com.yourpackage.codexterm.domain.usecase

import com.yourpackage.codexterm.domain.repository.HistoryRepository

class ObserveHistoryUseCase(
    private val historyRepository: HistoryRepository,
) {
    operator fun invoke() = historyRepository.observeHistory()
}
