package com.yugentech.sessions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

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

sealed class Screens(val route: String, val icon: ImageVector) {
    data object Appearance : Screens("appearance", Icons.Filled.ColorLens)
    data object SignUp : Screens("signUp", Icons.AutoMirrored.Filled.Login)
    data object SignIn : Screens("signIn", Icons.AutoMirrored.Filled.Login)
    data object Main : Screens("main", Icons.Filled.Home)
    data object About : Screens("about", Icons.Filled.Info)
    data object EditProfile : Screens("editProfile", Icons.Filled.Info)
    data object Settings : Screens("settings", Icons.Filled.Settings)
}