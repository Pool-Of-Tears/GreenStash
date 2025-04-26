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


package com.starry.greenstash.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.starry.greenstash.ui.screens.archive.composables.ArchiveScreen
import com.starry.greenstash.ui.screens.backups.composables.BackupScreen
import com.starry.greenstash.ui.screens.dwscreen.composables.DWScreen
import com.starry.greenstash.ui.screens.home.composables.HomeScreen
import com.starry.greenstash.ui.screens.info.composables.GoalInfoScreen
import com.starry.greenstash.ui.screens.input.composables.InputScreen
import com.starry.greenstash.ui.screens.other.CongratsScreen
import com.starry.greenstash.ui.screens.settings.composables.AboutScreen
import com.starry.greenstash.ui.screens.settings.composables.GoalCardStyle
import com.starry.greenstash.ui.screens.settings.composables.OSLScreen
import com.starry.greenstash.ui.screens.settings.composables.SettingsScreen
import com.starry.greenstash.ui.screens.welcome.composables.WelcomeScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: BaseScreen
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)

    ) {
        /** Welcome Screen */
        composable<OtherScreens.WelcomeScreen>(
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
        ) {
            WelcomeScreen(navController = navController)
        }

        /** Home Screen */
        composable<DrawerScreens.Home>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            HomeScreen(navController)
        }

        /** Deposit Withdraw Screen */
        composable<OtherScreens.DWScreen>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<OtherScreens.DWScreen>()
            DWScreen(
                goalId = args.goalId,
                transactionTypeName = args.transactionType,
                navController = navController
            )
        }

        /** Goal Info Screen */
        composable<OtherScreens.GoalInfoScreen>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<OtherScreens.GoalInfoScreen>()
            GoalInfoScreen(goalId = args.goalId, navController = navController)
        }

        /** Input Screen */
        composable<OtherScreens.InputScreen>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<OtherScreens.InputScreen>()
            InputScreen(editGoalId = args.goalId, navController = navController)
        }

        /** Goal Achieved Screen */
        composable<OtherScreens.CongratsScreen>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            CongratsScreen(navController = navController)
        }

        /** Archive Screen */
        composable<DrawerScreens.Archive>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ArchiveScreen(navController = navController)
        }

        /** Backup Screen */
        composable<DrawerScreens.Backups>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            BackupScreen(navController = navController)
        }

        /** Settings Screen */
        composable<DrawerScreens.Settings>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            SettingsScreen(navController = navController)
        }

        /** Goal Ui Settings Screen */
        composable<OtherScreens.GoalCardStyleScreen>(
            enterTransition = { enterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            GoalCardStyle(navController = navController)
        }

        /** Open Source Licenses Screen */
        composable<OtherScreens.OSLScreen>(
            enterTransition = { enterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            OSLScreen(navController = navController)
        }

        /** About Screen */
        composable<OtherScreens.AboutScreen>(
            enterTransition = { enterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            AboutScreen(navController = navController)
        }

    }
}