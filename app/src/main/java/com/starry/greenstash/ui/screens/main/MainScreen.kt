/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.ui.screens.main

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.starry.greenstash.MainActivity
import com.starry.greenstash.MainViewModel
import com.starry.greenstash.ui.navigation.NavGraph
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.other.AppLockedScreen
import com.starry.greenstash.ui.screens.settings.ThemeMode
import com.starry.greenstash.ui.theme.AdjustEdgeToEdge

/**
 * Main (container) screen of the app which contains the navigation graph
 * and authentication screen if app is locked.
 */
@Composable
fun MainScreen(
    activity: MainActivity,
    showAppContents: Boolean,
    startDestination: String,
    currentThemeMode: ThemeMode,
    onAuthRequest: () -> Unit,
) {
    // fix status bar icon color in dark mode.
    AdjustEdgeToEdge(activity = activity, themeState = currentThemeMode)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()

        Crossfade(
            targetState = showAppContents,
            label = "AppLockCrossFade",
            animationSpec = tween(500)
        ) { showAppContents ->
            // show app contents only if user has authenticated.
            if (showAppContents) {
                NavGraph(navController = navController, startDestination)
                // Handle and navigate to shortcut destinations after the UI is
                // properly loaded.
                val shouldHandleShortCut = remember { mutableStateOf(false) }
                LaunchedEffect(key1 = true) {
                    shouldHandleShortCut.value = true
                }
                if (shouldHandleShortCut.value) {
                    HandleShortcutIntent(activity.intent, navController)
                }
            } else {
                // show app locked screen if user has not authenticated.
                AppLockedScreen(onAuthRequest = onAuthRequest)
            }
        }
    }
}

@Composable
private fun HandleShortcutIntent(intent: Intent, navController: NavController) {
    val data = intent.data
    if (data != null && data.scheme == MainViewModel.LAUNCHER_SHORTCUT_SCHEME) {
        val goalId = intent.getLongExtra(MainViewModel.LC_SHORTCUT_GOAL_ID, -100)
        if (goalId != -100L) {
            navController.navigate(Screens.GoalInfoScreen.withGoalId(goalId.toString()))
            return
        }
        if (intent.getBooleanExtra(MainViewModel.LC_SHORTCUT_NEW_GOAL, false)) {
            navController.navigate(Screens.InputScreen.route)
        }
    }
}