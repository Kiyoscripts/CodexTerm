package com.yourpackage.codexterm.util

import android.content.Context
import com.yourpackage.codexterm.domain.model.TerminalLine
import com.yourpackage.codexterm.domain.model.TerminalStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class ShellExecutor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun execute(command: String): Flow<TerminalLine> = channelFlow {
        val process = ProcessBuilder("sh", "-c", command)
            .directory(context.filesDir)
            .apply {
                environment().clear()
                environment()["HOME"] = context.filesDir.absolutePath
                environment()["TMPDIR"] = context.cacheDir.absolutePath
                environment()["PATH"] = "/system/bin:/system/xbin"
            }
            .start()

        trySend(TerminalLine("Executing in app sandbox", TerminalStream.SYSTEM))

        val stdoutJob = launch(ioDispatcher) {
            streamToChannel(process.inputStream, TerminalStream.STDOUT)
        }
        val stderrJob = launch(ioDispatcher) {
            streamToChannel(process.errorStream, TerminalStream.STDERR)
        }
        val exitJob = launch(ioDispatcher) {
            val exitCode = process.waitFor()
            stdoutJob.join()
            stderrJob.join()
            trySend(TerminalLine("Process finished with exit code $exitCode", TerminalStream.SYSTEM))
            close()
        }

        awaitClose {
            exitJob.cancel()
            process.destroy()
        }
    }

    private fun kotlinx.coroutines.channels.ProducerScope<TerminalLine>.streamToChannel(
        inputStream: InputStream,
        stream: TerminalStream,
    ) {
        BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
            lines.forEach { line ->
                trySendBlocking(TerminalLine(text = line, stream = stream))
            }
        }
    }
}
