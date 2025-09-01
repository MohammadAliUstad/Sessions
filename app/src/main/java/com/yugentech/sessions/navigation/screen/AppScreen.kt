package com.yugentech.sessions.navigation.screens

// Defines all available navigation routes in the application
sealed class AppScreens(val route: String) {
    data object Onboarding : AppScreens("onboarding")
    data object Insights : AppScreens("insights")
    data object Appearance : AppScreens("appearance")
    data object SignUp : AppScreens("sign_up")
    data object SignIn : AppScreens("sign_in")
    data object Main : AppScreens("main")
    data object About : AppScreens("about")
    data object EditProfile : AppScreens("edit_profile")
    data object Settings : AppScreens("settings")
    data object Licenses : AppScreens("licenses")
}