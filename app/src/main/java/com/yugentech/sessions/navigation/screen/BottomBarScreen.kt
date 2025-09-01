package com.yugentech.sessions.navigation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Defines the main bottom navigation screens with icons
sealed class BottomBarScreens(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomBarScreens(
        route = "main",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Profile : BottomBarScreens(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle
    )

    data object Settings : BottomBarScreens(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        // Maps a string route to its corresponding screen object
        fun fromRoute(route: String): BottomBarScreens = when (route) {
            Home.route -> Home
            Profile.route -> Profile
            Settings.route -> Settings
            else -> Home
        }
    }
}