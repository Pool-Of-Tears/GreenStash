package com.starry.greenstash.ui.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.starry.greenstash.ui.navigation.NavGraph
import com.starry.greenstash.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun MainScreen(settingsViewModel: SettingsViewModel) {
    val systemUiController = rememberSystemUiController()
    val navController = rememberAnimatedNavController()

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
        darkIcons = settingsViewModel.getCurrentTheme() == ThemeMode.Light
    )

    Scaffold {
        NavGraph(navController = navController, paddingValues = it)
    }
}