package com.yugentech.sessions.navigation.navgraph

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yugentech.sessions.navigation.screen.AppScreen
import com.yugentech.sessions.notification.viewmodel.NotificationViewModel
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import com.yugentech.sessions.ui.dash.mainScreen.MainScreen
import com.yugentech.sessions.ui.auth.onboardingScreen.OnboardingScreen
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

fun NavGraphBuilder.dashGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onOnboardingComplete: () -> Unit,
    context: Context
) {
    // Defines the onboarding screen
    composable(
        route = AppScreen.Onboarding.route
    ) {
        OnboardingScreen(onFinish = onOnboardingComplete)
        BackHandler { (context as? Activity)?.finish() }
    }

    // The onboarding again, from Settings: finishing, skipping or backing out returns there
    composable(route = AppScreen.OnboardingReplay.route) {
        OnboardingScreen(
            onFinish = { navController.popBackStack() },
            finishLabel = stringResource(R.string.onboarding_done)
        )
    }

    // Defines the main dashboard screen
    composable(AppScreen.Main.route) {
        Timber.v("Composing Main Screen")
        val authState by authViewModel.authState.collectAsStateWithLifecycle()
        val currentUserId = authState.userId

        val homeViewModel: HomeViewModel = koinViewModel()
        val notificationViewModel: NotificationViewModel = koinViewModel()
        val profileViewModel: ProfileViewModel = koinViewModel()
        val timerViewModel: TimerViewModel = koinViewModel()
        val alertsViewModel: AlertsViewModel = koinViewModel()

        if (currentUserId != null) {
            MainScreen(
                userId = currentUserId,
                isGuest = authState.isGuest,
                onSignOut = {
                    Timber.i("User requested Sign Out")
                    timerViewModel.onLeave()
                    timerViewModel.updateSessionTask("")
                    notificationViewModel.cancelReminders()
                    // A guest has no account to come back to, so leaving removes their data
                    if (authState.isGuest) authViewModel.exitGuestMode() else authViewModel.signOut()
                },
                onExit = {
                    Timber.i("User requested App Exit")
                    timerViewModel.onLeave()
                    (context as? Activity)?.finish()
                },
                homeViewModel = homeViewModel,
                profileViewModel = profileViewModel,
                timerViewModel = timerViewModel,
                alertsViewModel = alertsViewModel,
                notificationViewModel = notificationViewModel,
                onAbout = {
                    navController.navigate(AppScreen.About.route) {
                        launchSingleTop = true
                    }
                },
                onWhatsNew = {
                    navController.navigate(AppScreen.WhatsNew.route) {
                        launchSingleTop = true
                    }
                },
                onMoreApps = {
                    navController.navigate(AppScreen.MoreApps.route) {
                        launchSingleTop = true
                    }
                },
                onAppearance = {
                    navController.navigate(AppScreen.Appearance.route) {
                        launchSingleTop = true
                    }
                },
                onHomeCustomization = {
                    navController.navigate(AppScreen.HomeCustomization.route) {
                        launchSingleTop = true
                    }
                },
                onLanguage = {
                    navController.navigate(AppScreen.Language.route) {
                        launchSingleTop = true
                    }
                },
                onReplayOnboarding = {
                    navController.navigate(AppScreen.OnboardingReplay.route) {
                        launchSingleTop = true
                    }
                },
                onViewInsights = {
                    navController.navigate(AppScreen.Insights.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateRoute = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}