package com.yugentech.sessions.navigation.screen

// Defines all available navigation routes in the application
sealed class AppScreen(val route: String) {
    data object Onboarding : AppScreen("onboarding")
    data object Insights : AppScreen("insights")
    data object Appearance : AppScreen("appearance")
    data object SignUp : AppScreen("sign_up")
    data object SignIn : AppScreen("sign_in")
    data object Main : AppScreen("main")
    data object About : AppScreen("about")
    data object EditProfile : AppScreen("edit_profile")
    data object Settings : AppScreen("settings")
    data object Licenses : AppScreen("licenses")
}