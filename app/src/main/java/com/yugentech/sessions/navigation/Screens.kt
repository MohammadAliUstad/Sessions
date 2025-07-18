package com.yugentech.sessions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreens(val route: String, val title: String, val icon: ImageVector) {
    data object Home : AppScreens("main", "Home", Icons.Filled.Home)
    data object Profile : AppScreens("profile", "Profile", Icons.Filled.AccountCircle)
    data object Leaderboard : AppScreens("leaderboard", "Leaderboard", Icons.Filled.Leaderboard)

    companion object {
        fun fromRoute(route: String): AppScreens = when (route) {
            Home.route -> Home
            Profile.route -> Profile
            Leaderboard.route -> Leaderboard
            else -> Home
        }
    }
}

sealed class Screens(val route: String, val title: String, val icon: ImageVector) {
    data object Login : Screens("login", "Login", Icons.AutoMirrored.Filled.Login)
    data object Home : Screens("main", "Home", Icons.Filled.Home)
    data object Profile : Screens("profile", "Profile", Icons.Filled.AccountCircle)
    data object Leaderboard : Screens("leaderboard", "Leaderboard", Icons.Filled.Leaderboard)
    data object About : Screens("about", "About", Icons.Filled.Info)
    data object EditProfile : Screens("editProfile", "EditProfile", Icons.Filled.Info)

    companion object {
        fun fromRoute(route: String): Screens = when (route) {
            Home.route -> Home
            Profile.route -> Profile
            Leaderboard.route -> Leaderboard
            else -> Home
        }
    }
}