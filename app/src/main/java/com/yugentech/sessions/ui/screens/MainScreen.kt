package com.yugentech.sessions.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.yugentech.sessions.authentication.AuthViewModel
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.status.StatusViewModel
import com.yugentech.sessions.ui.components.mainScreen.BottomNavBar
import com.yugentech.sessions.ui.components.mainScreen.TopAppBar
import com.yugentech.sessions.ui.screens.appScreens.HomeScreen
import com.yugentech.sessions.ui.screens.appScreens.LeaderboardScreen
import com.yugentech.sessions.ui.screens.appScreens.ProfileScreen
import com.yugentech.sessions.viewModels.LeaderboardViewModel

private const val ANIMATION_DURATION = 300
private const val FADE_DURATION = 150
private val bottomNavItems = listOf(AppScreens.Leaderboard, AppScreens.Home, AppScreens.Profile)
private val screenSaver = Saver<AppScreens, String>(
    save = { it.route },
    restore = { route -> AppScreens.fromRoute(route) }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: String,
    sessionViewModel: SessionViewModel,
    statusViewModel: StatusViewModel,
    authViewModel: AuthViewModel,
    leaderboardViewModel: LeaderboardViewModel,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    var currentScreen by rememberSaveable(stateSaver = screenSaver) {
        mutableStateOf(AppScreens.Home)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                onNavigateToAbout = onNavigateToAbout,
                onLogout = onLogout,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavBar(
                items = bottomNavItems,
                currentScreen = currentScreen,
                onSelected = { screen -> currentScreen = screen }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                when {
                    // Leaderboard -> Home: slide right
                    initialState == AppScreens.Leaderboard && targetState == AppScreens.Home ->
                        createSlideTransition(slideFromRight = true)

                    // Profile -> Home: slide left
                    initialState == AppScreens.Profile && targetState == AppScreens.Home ->
                        createSlideTransition(slideFromRight = false)

                    // Home -> Leaderboard: slide left
                    initialState == AppScreens.Home && targetState == AppScreens.Leaderboard ->
                        createSlideTransition(slideFromRight = false)

                    // Home -> Profile: slide right
                    initialState == AppScreens.Home && targetState == AppScreens.Profile ->
                        createSlideTransition(slideFromRight = true)

                    // Leaderboard <-> Profile: fade only (skip home)
                    else -> createFadeTransition()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                AppScreens.Home -> HomeScreen(
                    sessionViewModel = sessionViewModel,
                    statusViewModel = statusViewModel,
                    userId = userId
                )

                AppScreens.Profile -> ProfileScreen(
                    authViewModel = authViewModel,
                    sessionViewModel = sessionViewModel,
                    onEditProfile = onEditProfile
                )

                AppScreens.Leaderboard -> LeaderboardScreen(
                    leaderboardViewModel = leaderboardViewModel
                )
            }
        }
    }
}

// Performance optimization: Reusable slide transition
private fun createSlideTransition(slideFromRight: Boolean): ContentTransform {
    val offsetMultiplier = if (slideFromRight) 1 else -1
    return slideInHorizontally(
        animationSpec = tween(ANIMATION_DURATION),
        initialOffsetX = { it * offsetMultiplier }
    ).plus(fadeIn(animationSpec = tween(ANIMATION_DURATION)))
        .togetherWith(
            slideOutHorizontally(
                animationSpec = tween(ANIMATION_DURATION),
                targetOffsetX = { -it * offsetMultiplier }
            ).plus(fadeOut(animationSpec = tween(ANIMATION_DURATION)))
        )
}

// Performance optimization: Reusable fade transition
private fun createFadeTransition(): ContentTransform {
    return fadeIn(animationSpec = tween(FADE_DURATION))
        .togetherWith(fadeOut(animationSpec = tween(FADE_DURATION)))
}