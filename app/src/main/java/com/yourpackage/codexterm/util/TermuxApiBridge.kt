package com.yourpackage.codexterm.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.yourpackage.codexterm.domain.model.TerminalLine
import com.yourpackage.codexterm.domain.model.TerminalStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TermuxApiBridge(
    private val context: Context,
) {
    private val termuxPackage = "com.termux"
    private val termuxApiPackage = "com.termux.api"

    fun isTermuxInstalled(): Boolean {
        return isPackageInstalled(termuxPackage) && isPackageInstalled(termuxApiPackage)
    }

    fun runCommand(command: String): Flow<TerminalLine> = flow {
        emit(TerminalLine("Dispatching command to Termux:API", TerminalStream.SYSTEM))
        val intent = Intent("com.termux.api.RUN_COMMAND").apply {
            `package` = termuxApiPackage
            putExtra("com.termux.api.extra_command", command)
            putExtra("com.termux.api.extra_background", true)
            putExtra("com.termux.api.extra_workdir", "/data/data/com.termux/files/home")
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                Intent("${context.packageName}.TERMUX_RESULT"),
                flags,
            )
            putExtra("com.termux.api.extra_result_pending_intent", pendingIntent)
        }
        context.sendBroadcast(intent)
        emit(
            TerminalLine(
                text = "Termux:API accepted the command request. Output capture depends on the installed Termux plugins.",
                stream = TerminalStream.SYSTEM,
            ),
        )
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
        }.isSuccess
    }
}
