package com.yugentech.sessions.navigation.screen

import androidx.annotation.StringRes
import com.yugentech.sessions.R

// Defines the main bottom navigation screens
sealed class BottomBarScreen(
    val route: String,
    @StringRes val title: Int
) {
    data object Home : BottomBarScreen(
        route = "main",
        title = R.string.nav_home
    )

    data object Profile : BottomBarScreen(
        route = "profile",
        title = R.string.nav_profile
    )

    data object Settings : BottomBarScreen(
        route = "settings",
        title = R.string.nav_settings
    )

    companion object {
        // Maps a string route to its corresponding screen object
        fun fromRoute(route: String): BottomBarScreen = when (route) {
            Home.route -> Home
            Profile.route -> Profile
            Settings.route -> Settings
            else -> Home
        }
    }
}