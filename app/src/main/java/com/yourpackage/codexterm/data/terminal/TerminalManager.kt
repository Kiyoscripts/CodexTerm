package com.yourpackage.codexterm.data.terminal

import android.content.Context
import com.yourpackage.codexterm.domain.model.TerminalBackend
import com.yourpackage.codexterm.domain.model.TerminalLine
import com.yourpackage.codexterm.util.ShellExecutor
import com.yourpackage.codexterm.util.TermuxApiBridge
import kotlinx.coroutines.flow.Flow

class TerminalManager(
    context: Context,
) {
    private val termuxApiBridge = TermuxApiBridge(context)
    private val shellExecutor = ShellExecutor(context)

    fun backend(): TerminalBackend {
        return if (termuxApiBridge.isTermuxInstalled()) {
            TerminalBackend.TERMUX_API
        } else {
            TerminalBackend.IN_APP_SHELL
        }
    }

    fun execute(command: String): Flow<TerminalLine> {
        return if (backend() == TerminalBackend.TERMUX_API) {
            termuxApiBridge.runCommand(command)
        } else {
            shellExecutor.execute(command)
        }
    }
}
