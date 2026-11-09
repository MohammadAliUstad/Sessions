package com.yugentech.sessions.navigation.screen

// Defines all available navigation routes in the application
sealed class AppScreen(val route: String) {
    data object Onboarding : AppScreen("onboarding")
    // The same onboarding, opened again from Settings: finishing it just goes back
    data object OnboardingReplay : AppScreen("onboarding_replay")
    data object Insights : AppScreen("insights")
    data object Appearance : AppScreen("appearance")
    data object HomeCustomization : AppScreen("home_customization")
    data object SignUp : AppScreen("sign_up")
    data object SignIn : AppScreen("sign_in")
    data object Main : AppScreen("main")
    data object About : AppScreen("about")
    data object MoreApps : AppScreen("more_apps")
    data object Settings : AppScreen("settings")
    data object Licenses : AppScreen("licenses")
    data object WhatsNew : AppScreen("whats_new")
}