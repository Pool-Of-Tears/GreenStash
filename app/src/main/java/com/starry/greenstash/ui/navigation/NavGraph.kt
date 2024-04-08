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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.starry.greenstash.ui.screens.backups.BackupScreen
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


private const val NAVIGATION_ANIM_DURATION = 300

private fun enterTransition() = slideInHorizontally(
    initialOffsetX = { NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeIn(animationSpec = tween(NAVIGATION_ANIM_DURATION))

private fun exitTransition() = slideOutHorizontally(
    targetOffsetX = { -NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeOut(animationSpec = tween(NAVIGATION_ANIM_DURATION))

private fun popEnterTransition() = slideInHorizontally(
    initialOffsetX = { -NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeIn(animationSpec = tween(NAVIGATION_ANIM_DURATION))

private fun popExitTransition() = slideOutHorizontally(
    targetOffsetX = { NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeOut(animationSpec = tween(NAVIGATION_ANIM_DURATION))


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)

    ) {
        /** Welcome Screen */
        composable(
            route = Screens.WelcomeScreen.route,
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
        ) {
            WelcomeScreen(navController = navController)
        }

        /** Home Screen */
        composable(
            route = DrawerScreens.Home.route,
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
        ) {
            HomeScreen(navController)
        }

        /** Deposit Withdraw Screen */
        composable(
            route = Screens.DWScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
            arguments = listOf(
                navArgument(DW_GOAL_ID_ARG_KEY) {
                    type = NavType.StringType
                },
            ),
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments!!.getString(DW_GOAL_ID_ARG_KEY)!!
            val transactionType =
                backStackEntry.arguments!!.getString(DW_TRANSACTION_TYPE_ARG_KEY)!!
            DWScreen(
                goalId = goalId,
                transactionTypeName = transactionType,
                navController = navController
            )
        }

        /** Goal Info Screen */
        composable(
            route = Screens.GoalInfoScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
            arguments = listOf(
                navArgument(GOAL_INFO_ARG_KEY) {
                    type = NavType.StringType
                },
            ),
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments!!.getString(GOAL_INFO_ARG_KEY)!!
            GoalInfoScreen(goalId = goalId, navController = navController)
        }

        /** Input Screen */
        composable(
            route = Screens.InputScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
            arguments = listOf(navArgument(EDIT_GOAL_ARG_KEY) {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val editGoalId = backStackEntry.arguments!!.getString(EDIT_GOAL_ARG_KEY)
            InputScreen(editGoalId = editGoalId, navController = navController)
        }

        /** Goal Achieved Screen */
        composable(
            route = Screens.CongratsScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            CongratsScreen(navController = navController)
        }

        /** Backup Screen */
        composable(
            route = DrawerScreens.Backups.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            BackupScreen(navController = navController)
        }

        /** Settings Screen */
        composable(
            route = DrawerScreens.Settings.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            SettingsScreen(navController = navController)
        }

        /** Goal Ui Settings Screen */
        composable(
            route = Screens.GoalCardStyle.route,
            enterTransition = { enterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            GoalCardStyle(navController = navController)
        }

        /** Open Source Licenses Screen */
        composable(
            route = Screens.OSLScreen.route,
            enterTransition = { enterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            OSLScreen(navController = navController)
        }

        /** About Screen */
        composable(
            route = Screens.AboutScreen.route,
            enterTransition = { enterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            AboutScreen(navController = navController)
        }

    }
}