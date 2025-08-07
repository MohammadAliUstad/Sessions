package com.yugentech.sessions.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.sessions.SessionsViewModel
import com.yugentech.sessions.ui.components.mainScreen.BottomNavBar
import com.yugentech.sessions.ui.components.mainScreen.TopAppBar
import com.yugentech.sessions.ui.screens.appScreens.HomeScreen
import com.yugentech.sessions.ui.screens.appScreens.ProfileScreen
import com.yugentech.sessions.user.UserViewModel

private const val ANIMATION_DURATION = 300
private const val FADE_DURATION = 150

private val bottomNavItems = listOf(AppScreens.Home, AppScreens.Profile)

private val screenSaver = Saver<AppScreens, String>(
    save = { it.route },
    restore = { route -> AppScreens.fromRoute(route) }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: String,
    userViewModel: UserViewModel,
    sessionsViewModel: SessionsViewModel,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    var currentScreen by rememberSaveable(stateSaver = screenSaver) {
        mutableStateOf(AppScreens.Home)
    }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior() // Enhanced scroll behavior

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                onLogout = onLogout,
                onSettings = onSettings,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavBar(
                items = bottomNavItems,
                currentScreen = currentScreen,
                onSelected = { screen ->
                    if (screen != currentScreen) {
                        currentScreen = screen
                    }
                }
            )
        },
    ) { innerPadding ->

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                createScreenTransition(
                    fromHome = initialState == AppScreens.Home,
                    toHome = targetState == AppScreens.Home
                )
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                AppScreens.Home -> HomeScreen(
                    sessionsViewModel = sessionsViewModel,
                    userId = userId
                )

                AppScreens.Profile -> ProfileScreen(
                    userId = userId,
                    sessionsViewModel = sessionsViewModel,
                    onEditProfile = onEditProfile,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

// Simplified and more readable transition logic
private fun createScreenTransition(
    fromHome: Boolean,
    toHome: Boolean
): ContentTransform {
    return when {
        fromHome && !toHome -> createSlideTransition(slideFromRight = true)  // Home → Profile
        !fromHome && toHome -> createSlideTransition(slideFromRight = false) // Profile → Home
        else -> createFadeTransition() // Fallback
    }
}

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

private fun createFadeTransition(): ContentTransform {
    return fadeIn(animationSpec = tween(FADE_DURATION))
        .togetherWith(fadeOut(animationSpec = tween(FADE_DURATION)))
}