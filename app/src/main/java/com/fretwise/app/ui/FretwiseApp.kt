package com.fretwise.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fretwise.app.FretwiseViewModel
import com.fretwise.app.ui.finder.ChordFinderScreen
import com.fretwise.app.ui.settings.SettingsScreen
import com.fretwise.app.ui.welcome.WelcomeScreen

private object Routes {
    const val WELCOME = "welcome"
    const val FINDER = "finder"
    const val SETTINGS = "settings"
}

@Composable
fun FretwiseApp(viewModel: FretwiseViewModel = viewModel()) {
    val navController = rememberNavController()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val frets by viewModel.frets.collectAsStateWithLifecycle()
    val baseFret by viewModel.baseFret.collectAsStateWithLifecycle()
    // Decided once: finishing onboarding must not swap the graph's start under the user.
    val startDestination = remember { if (settings.onboarded) Routes.FINDER else Routes.WELCOME }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onGetStarted = {
                        viewModel.updateSettings { it.copy(onboarded = true) }
                        navController.navigate(Routes.FINDER) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.FINDER) {
                ChordFinderScreen(
                    settings = settings,
                    frets = frets,
                    baseFret = baseFret,
                    onSetFret = viewModel::setFret,
                    onShiftWindow = viewModel::shiftWindow,
                    onClear = viewModel::clearFrets,
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) { launchSingleTop = true } },
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    settings = settings,
                    onUpdate = viewModel::updateSettings,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
