package com.starry.greenstash.ui.navigation

import com.starry.greenstash.R

sealed class DrawerScreens(val route: String, val name: String, val icon: Int) {
    object Home : DrawerScreens("home", "Home", R.drawable.ic_nav_home)
    object Settings : DrawerScreens("settings", "Settings", R.drawable.ic_nav_settings)
    object Backups : DrawerScreens("backups", "Backups", R.drawable.ic_nav_backups)
}


