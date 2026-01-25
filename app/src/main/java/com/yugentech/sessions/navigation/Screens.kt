package com.yugentech.sessions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Defines the main bottom navigation screens with icons
sealed class AppScreens(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : AppScreens(
        route = "main",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Profile : AppScreens(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle
    )

    data object Settings : AppScreens(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        // Maps a string route to its corresponding screen object
        fun fromRoute(route: String): AppScreens = when (route) {
            Home.route -> Home
            Profile.route -> Profile
            Settings.route -> Settings
            else -> Home
        }
    }
}

// Defines all available navigation routes in the application
sealed class Screens(val route: String) {
    data object Onboarding : Screens("onboarding")
    data object Insights : Screens("insights")
    data object Appearance : Screens("appearance")
    data object SignUp : Screens("signUp")
    data object SignIn : Screens("signIn")
    data object Main : Screens("main")
    data object About : Screens("about")
    data object EditProfile : Screens("editProfile")
    data object Settings : Screens("settings")
    data object Licenses : Screens("licenses")
}