package com.starry.greenstash.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.starry.greenstash.ui.screens.home.composables.HomeScreen

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues) {

    AnimatedNavHost(
        navController = navController,
        startDestination = DrawerScreens.Home.route,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        /** Home Screen */
        composable(
            route = DrawerScreens.Home.route,
        ) {
            HomeScreen(navController)
        }

    }
}