package com.starry.greenstash.ui.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.starry.greenstash.ui.navigation.NavGraph
import com.starry.greenstash.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun MainScreen(settingsViewModel: SettingsViewModel) {
    val systemUiController = rememberSystemUiController()
    val navController = rememberAnimatedNavController()

    systemUiController.setNavigationBarColor(
        color = MaterialTheme.colorScheme.background,
        darkIcons = settingsViewModel.getCurrentTheme() == ThemeMode.Light
    )

    systemUiController.setStatusBarColor(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        darkIcons = settingsViewModel.getCurrentTheme() == ThemeMode.Light
    )

    Scaffold {
        NavGraph(navController = navController, paddingValues = it)
    }
}