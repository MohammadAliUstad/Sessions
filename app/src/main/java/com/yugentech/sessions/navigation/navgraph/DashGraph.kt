package com.yugentech.sessions.navigation.navgraph

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yugentech.sessions.auth.state.AuthState
import com.yugentech.sessions.navigation.screen.AppScreen
import com.yugentech.sessions.notification.viewmodel.NotificationsViewModel
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import com.yugentech.sessions.ui.dash.mainScreen.MainScreen
import com.yugentech.sessions.ui.auth.onboardingScreen.OnboardingScreen
import com.yugentech.sessions.ui.dash.util.defaultExitTransition
import com.yugentech.sessions.ui.dash.util.defaultPopExitTransition
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

fun NavGraphBuilder.dashGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    authState: AuthState,
    onOnboardingComplete: () -> Unit,
    context: Context
) {
    // Defines the onboarding screen
    composable(
        route = AppScreen.Onboarding.route,
        exitTransition = { defaultExitTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        OnboardingScreen(onFinish = onOnboardingComplete)
        BackHandler { (context as? Activity)?.finish() }
    }

    // Defines the main dashboard screen
    composable(AppScreen.Main.route) {
        Timber.v("Composing Main Screen")
        val authState by authViewModel.authState.collectAsStateWithLifecycle()
        val currentUserId = authState.userId

        val homeViewModel: HomeViewModel = koinViewModel()
        val notificationsViewModel: NotificationsViewModel = koinViewModel()
        val profileViewModel: ProfileViewModel = koinViewModel()
        val timerViewModel: TimerViewModel = koinViewModel()
        val alertsViewModel: AlertsViewModel = koinViewModel()

        if (currentUserId != null) {
            MainScreen(
                userId = currentUserId,
                onSignOut = {
                    Timber.i("User requested Sign Out")
                    timerViewModel.onLeave()
                    timerViewModel.updateSessionTask("")
                    notificationsViewModel.cancelReminders()
                    authViewModel.signOut()
                },
                onExit = {
                    Timber.i("User requested App Exit")
                    timerViewModel.onLeave()
                    (context as? Activity)?.finish()
                },
                onEditProfile = {
                    navController.navigate(AppScreen.EditProfile.route) {
                        launchSingleTop = true
                    }
                },
                homeViewModel = homeViewModel,
                profileViewModel = profileViewModel,
                timerViewModel = timerViewModel,
                alertsViewModel = alertsViewModel,
                notificationsViewModel = notificationsViewModel,
                onAbout = {
                    navController.navigate(AppScreen.About.route) {
                        launchSingleTop = true
                    }
                },
                onAppearance = {
                    navController.navigate(AppScreen.Appearance.route) {
                        launchSingleTop = true
                    }
                },
                onViewInsights = {
                    navController.navigate(AppScreen.Insights.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}