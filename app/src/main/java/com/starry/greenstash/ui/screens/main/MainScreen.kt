package com.starry.greenstash.ui.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.starry.greenstash.ui.navigation.NavGraph

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun MainScreen() {
    val systemUiController = rememberSystemUiController()
    val navController = rememberAnimatedNavController()

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
        darkIcons = !isSystemInDarkTheme()
    )

    Scaffold {
        NavGraph(navController = navController, paddingValues = it)
    }
}