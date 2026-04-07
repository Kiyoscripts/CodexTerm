package com.yourpackage.codexterm.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yourpackage.codexterm.AppContainer
import com.yourpackage.codexterm.domain.model.PromptHistoryEntry
import com.yourpackage.codexterm.ui.navigation.CodexTermDestination
import com.yourpackage.codexterm.ui.screen.HomeScreen
import com.yourpackage.codexterm.ui.screen.PromptScreen
import com.yourpackage.codexterm.ui.screen.TerminalScreen

@Composable
fun CodexTermApp(
    appContainer: AppContainer,
) {
    val navController = rememberNavController()
    val promptViewModel: PromptViewModel = viewModel(
        factory = PromptViewModel.factory(
            appContainer.generateCodeUseCase,
            appContainer.saveHistoryEntryUseCase,
        ),
    )
    val terminalViewModel: TerminalViewModel = viewModel(
        factory = TerminalViewModel.factory(appContainer.terminalManager),
    )
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.factory(appContainer.observeHistoryUseCase),
    )

    val promptState by promptViewModel.uiState.collectAsState()
    val terminalState by terminalViewModel.uiState.collectAsState()
    val homeState by homeViewModel.uiState.collectAsState()
    val destinations = remember { CodexTermDestination.entries }
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(destination.label.take(1)) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = CodexTermDestination.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(CodexTermDestination.Home.route) {
                HomeScreen(
                    state = homeState,
                    onPromptSelected = { entry: PromptHistoryEntry ->
                        promptViewModel.updatePrompt(entry.prompt)
                        promptViewModel.updateLanguage(entry.language)
                        promptViewModel.updateGeneratedCode(entry.response)
                        terminalViewModel.loadGeneratedCodeAsCommand(entry.response)
                        navController.navigate(CodexTermDestination.Prompt.route)
                    },
                )
            }
            composable(CodexTermDestination.Prompt.route) {
                PromptScreen(
                    state = promptState,
                    onPromptChanged = promptViewModel::updatePrompt,
                    onLanguageSelected = promptViewModel::updateLanguage,
                    onGenerate = promptViewModel::generate,
                    onGeneratedCodeChanged = promptViewModel::updateGeneratedCode,
                    onSendToTerminal = {
                        terminalViewModel.loadGeneratedCodeAsCommand(promptState.generatedCode)
                        navController.navigate(CodexTermDestination.Terminal.route)
                    },
                )
            }
            composable(CodexTermDestination.Terminal.route) {
                TerminalScreen(
                    state = terminalState,
                    onCommandChanged = terminalViewModel::updateCommand,
                    onRunCommand = terminalViewModel::runCommand,
                    onClearOutput = terminalViewModel::clearOutput,
                )
            }
        }
    }
}
