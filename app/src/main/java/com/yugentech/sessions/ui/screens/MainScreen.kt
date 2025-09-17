package com.yugentech.sessions.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.animation.core.Transition
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.ui.components.homeScreen.ExitConfirmationDialog
import com.yugentech.sessions.ui.components.homeScreen.LogoutConfirmationDialog
import com.yugentech.sessions.ui.components.mainScreen.BottomNavBar
import com.yugentech.sessions.ui.components.mainScreen.TopAppBar
import com.yugentech.sessions.user.UserViewModel
import com.yugentech.sessions.utils.Constants.DEFAULT_ANIMATION_DURATION
import com.yugentech.sessions.utils.defaultEnterTransition
import com.yugentech.sessions.utils.defaultExitTransition
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel

private const val ANIMATION_DURATION = 300

private val bottomNavItems = listOf(AppScreens.Home, AppScreens.Profile)

private val screenSaver = Saver<AppScreens, String>(
    save = { it.route },
    restore = { route -> AppScreens.fromRoute(route) }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: String,
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    profileViewModel: ProfileViewModel,
    notificationsViewModel: NotificationsViewModel,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    var currentScreen by rememberSaveable(stateSaver = screenSaver) {
        mutableStateOf(AppScreens.Home)
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Handle back press based on current screen
    BackHandler {
        when (currentScreen) {
            AppScreens.Profile -> {
                currentScreen = AppScreens.Home
            }
            AppScreens.Home -> {
                showExitDialog = true
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = {
                showExitDialog = false
                (context as? Activity)?.finish()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                onLogout = { showLogoutDialog = true },
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            targetState = currentScreen,
            transitionSpec = {
                if(initialState == AppScreens.Home) {
                    defaultEnterTransition(DEFAULT_ANIMATION_DURATION)
                } else {
                    defaultExitTransition(DEFAULT_ANIMATION_DURATION)
                }
            }
        ) { screen ->
            when (screen) {
                AppScreens.Home -> HomeScreen(
                    userId = userId,
                    homeViewModel = homeViewModel,
                    notificationsViewModel = notificationsViewModel,
                )

                AppScreens.Profile -> ProfileScreen(
                    userId = userId,
                    onEditProfile = onEditProfile,
                    profileViewModel = profileViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

private fun createScreenTransition(
    fromHome: Boolean,
): Transition<> {
    return
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