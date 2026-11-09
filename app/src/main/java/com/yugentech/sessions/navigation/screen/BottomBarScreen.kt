package com.yugentech.sessions.navigation.screen

import androidx.annotation.StringRes
import com.yugentech.sessions.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Defines the main bottom navigation screens with icons
sealed class BottomBarScreen(
    val route: String,
    @StringRes val title: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomBarScreen(
        route = "main",
        title = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Profile : BottomBarScreen(
        route = "profile",
        title = R.string.nav_profile,
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle
    )

    data object Settings : BottomBarScreen(
        route = "settings",
        title = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
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