package com.yugentech.sessions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

// Represents the bottom navigation bar items
sealed class AppScreens(val route: String, val title: String, val icon: ImageVector) {
    data object Home : AppScreens("main", "Home", Icons.Filled.Home)
    data object Profile : AppScreens("profile", "Profile", Icons.Filled.AccountCircle)

    companion object {
        fun fromRoute(route: String): AppScreens = when (route) {
            Home.route -> Home
            Profile.route -> Profile
            else -> Home
        }
    }
}

// Represents all distinct navigation destinations in the app
sealed class Screens(val route: String) {
    data object Appearance : Screens("appearance")
    data object SignUp : Screens("signUp")
    data object SignIn : Screens("signIn")
    data object Main : Screens("main")
    data object About : Screens("about")
    data object EditProfile : Screens("editProfile")
    data object Settings : Screens("settings")
}