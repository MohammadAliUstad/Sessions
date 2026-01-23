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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.ui.dash.common.ExitConfirmationDialog
import com.yugentech.sessions.ui.dash.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.mainScreen.BottomNavBar
import com.yugentech.sessions.ui.dash.components.mainScreen.TopAppBar
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private val bottomNavItems = listOf(AppScreens.Profile, AppScreens.Home, AppScreens.Settings)

private val screenSaver = Saver<AppScreens, String>(
    save = { it.route },
    restore = { route -> AppScreens.fromRoute(route) }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: String,
    homeViewModel: HomeViewModel,
    timerViewModel: TimerViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    onSignOut: () -> Unit,
    onExit: () -> Unit,
    onEditProfile: () -> Unit,
    onAbout: () -> Unit,
    onAppearance: () -> Unit,
) {
    val context = LocalContext.current
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var hasCheckedPermission by rememberSaveable { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                toastMessage =
                    "Notification permission denied. Please enable notifications to see Session alerts."
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCheckedPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                delay(500)
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            hasCheckedPermission = true
        }
    }

    var currentScreen by rememberSaveable(stateSaver = screenSaver) {
        mutableStateOf(AppScreens.Home)
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (currentScreen != AppScreens.Home) {
            currentScreen = AppScreens.Home
        } else {
            showExitDialog = true
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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
                    onLogout = { },
                    onSettings = { currentScreen = AppScreens.Settings },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->

            AnimatedContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                targetState = currentScreen,
                transitionSpec = {
                    val screenOrder =
                        listOf(AppScreens.Profile, AppScreens.Home, AppScreens.Settings)
                    val initialIndex = screenOrder.indexOf(initialState)
                    val targetIndex = screenOrder.indexOf(targetState)

                    val animationDuration = AppAnimations.Durations.Standard

                    if (targetIndex > initialIndex) {
                        (slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(animationDuration)
                        ) + fadeIn(animationSpec = tween(animationDuration))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(animationDuration)
                                ) + fadeOut(animationSpec = tween(animationDuration)))
                    } else {
                        (slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(animationDuration)
                        ) + fadeIn(animationSpec = tween(animationDuration))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(animationDuration)
                                ) + fadeOut(animationSpec = tween(animationDuration)))
                    }
                }
            ) { screen ->
                when (screen) {
                    AppScreens.Home -> HomeScreen(
                        userId = userId,
                        timerViewModel = timerViewModel,
                        homeViewModel = homeViewModel
                    )

                    AppScreens.Profile -> ProfileScreen(
                        userId = userId,
                        onEditProfile = onEditProfile,
                        profileViewModel = profileViewModel
                    )

                    AppScreens.Settings -> SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        notificationsViewModel = notificationsViewModel,
                        onSignOut = onSignOut,
                        onAbout = onAbout,
                        onAppearance = onAppearance
                    )
                }
            }
        }

        BottomNavBar(
            items = bottomNavItems,
            currentScreen = currentScreen,
            onSelected = { screen ->
                if (screen != currentScreen) {
                    currentScreen = screen
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
        )

        ToastMessage(
            message = toastMessage,
            onDismiss = { toastMessage = null },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = MaterialTheme.spacing.xxl)
                .zIndex(3f)
        )
    }
}