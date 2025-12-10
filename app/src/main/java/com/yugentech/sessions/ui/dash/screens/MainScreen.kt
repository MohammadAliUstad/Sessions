package com.yugentech.sessions.ui.dash.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.homeScreen.ExitConfirmationDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.LogoutConfirmationDialog
import com.yugentech.sessions.ui.dash.components.mainScreen.BottomNavBar
import com.yugentech.sessions.ui.dash.components.mainScreen.TopAppBar
import com.yugentech.sessions.utils.defaultEnterTransition
import com.yugentech.sessions.utils.defaultExitTransition
import com.yugentech.sessions.utils.defaultPopEnterTransition
import com.yugentech.sessions.utils.defaultPopExitTransition
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import kotlinx.coroutines.delay

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
    profileViewModel: ProfileViewModel,
    notificationsViewModel: NotificationsViewModel,
    onSignOut: () -> Unit,
    onExit: () -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current

    var toastMessage by remember { mutableStateOf<String?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                toastMessage =
                    "Notification permission denied. Session alerts will not be displayed."
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                delay(2000)
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var currentScreen by rememberSaveable(stateSaver = screenSaver) {
        mutableStateOf(AppScreens.Home)
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

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
                onSignOut()
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
                onExit()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    if (initialState == AppScreens.Home && targetState == AppScreens.Profile) {
                        defaultEnterTransition() togetherWith defaultExitTransition()
                    } else if (initialState == AppScreens.Profile && targetState == AppScreens.Home) {
                        defaultPopEnterTransition() togetherWith defaultPopExitTransition()
                    } else {
                        fadeIn(animationSpec = tween(AppConstants.DEFAULT_ANIMATION_DURATION)) togetherWith
                                fadeOut(animationSpec = tween(AppConstants.DEFAULT_ANIMATION_DURATION))
                    }
                }
            ) { screen ->
                when (screen) {
                    AppScreens.Home -> HomeScreen(
                        userId = userId,
                        homeViewModel = homeViewModel
                    )

                    AppScreens.Profile -> ProfileScreen(
                        userId = userId,
                        onEditProfile = onEditProfile,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }

        ToastMessage(
            message = toastMessage,
            onDismiss = { toastMessage = null },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.spacing.xxl)
        )
    }
}