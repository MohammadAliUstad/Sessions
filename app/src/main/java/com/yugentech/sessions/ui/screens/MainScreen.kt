package com.yugentech.sessions.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.ui.components.mainScreen.TopAppBar
import com.yugentech.sessions.ui.screens.appScreens.HomeScreen
import com.yugentech.sessions.ui.screens.appScreens.ProfileScreen
import com.yugentech.sessions.sessions.SessionsViewModel
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior() // Enhanced scroll behavior

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
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                containerColor = Color(0xCCFFFEB3B), // pill color
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(30.dp),
                contentColor = MaterialTheme.colorScheme.onSurface,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0)
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