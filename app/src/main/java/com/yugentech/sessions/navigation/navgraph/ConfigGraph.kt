package com.yugentech.sessions.navigation.navgraph

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yugentech.sessions.auth.state.AuthState
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.navigation.screen.AppScreen
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.config.aboutScreen.AboutScreen
import com.yugentech.sessions.ui.config.appearanceScreen.AppearanceScreen
import com.yugentech.sessions.ui.config.attributionsScreen.AttributionsScreen
import com.yugentech.sessions.ui.config.editProfileScreen.EditProfileScreen
import com.yugentech.sessions.ui.config.insightsScreen.InsightsScreen
import com.yugentech.sessions.ui.dash.util.formatTime
import com.yugentech.sessions.viewModels.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

fun NavGraphBuilder.configGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    authState: AuthState
) {
    // Defines the appearance settings screen
    composable(AppScreen.Appearance.route) {
        val themeViewModel: ThemeViewModel = koinViewModel()
        AppearanceScreen(
            onNavigateBack = { navController.popBackStack() },
            themeViewModel = themeViewModel
        )
    }

    // Defines the about screen
    composable(AppScreen.About.route) {
        AboutScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToLicenses = {
                navController.navigate(AppScreen.Licenses.route) {
                    launchSingleTop = true
                }
            }
        )
    }

    // Defines the licenses/attributions screen
    composable(AppScreen.Licenses.route) {
        AttributionsScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // Defines the insights and statistics screen
    composable(AppScreen.Insights.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AppScreen.Main.route)
        }
        val profileViewModel: ProfileViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
        val currentUserId = authState.userId

        LaunchedEffect(currentUserId) {
            currentUserId?.let { profileViewModel.loadProfile(it) }
        }

        InsightsScreen(
            totalTime = formatTime(profileUiState.totalTime),
            taskDistribution = profileUiState.taskDistribution,
            onBack = { navController.popBackStack() },
            streakCount = profileUiState.streakCount,
            dailyVolume = profileUiState.dailyVolume,
            peakHour = profileUiState.peakHour,
            heatmapHistory = profileUiState.heatmapHistory
        )
    }

    // Defines the edit profile screen
    composable(AppScreen.EditProfile.route) {
        Timber.v("Composing EditProfile Screen")
        val authState by authViewModel.authState.collectAsStateWithLifecycle()
        val currentUserId = authState.userId
        val profileViewModel: ProfileViewModel = koinViewModel()

        if (currentUserId != null) {
            EditProfileScreen(
                profileViewModel = profileViewModel,
                userId = currentUserId,
                onNavigateBack = { navController.popBackStack() }
            )
        } else {
            Timber.w("Navigated to EditProfile without valid User ID")
            LaunchedEffect(Unit) { navController.popBackStack() }
        }
    }
}