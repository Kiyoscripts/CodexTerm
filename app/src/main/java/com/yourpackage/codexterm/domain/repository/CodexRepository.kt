package com.yourpackage.codexterm.domain.repository

import kotlinx.coroutines.flow.Flow

interface CodexRepository {
    fun generateCode(prompt: String, language: String): Flow<String>
}
