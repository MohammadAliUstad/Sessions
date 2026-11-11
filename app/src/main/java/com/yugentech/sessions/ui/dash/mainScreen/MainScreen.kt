package com.yugentech.sessions.ui.dash.mainScreen

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import com.yugentech.sessions.announcements.ui.AnnouncementHost
import com.yugentech.sessions.navigation.screen.BottomBarScreen
import com.yugentech.sessions.notification.viewmodel.NotificationViewModel
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import com.yugentech.sessions.ui.dash.homeScreen.HomeScreen
import com.yugentech.sessions.ui.dash.homeScreen.components.ExitConfirmationDialog
import com.yugentech.sessions.ui.dash.mainScreen.components.BottomNavBar
import com.yugentech.sessions.ui.dash.mainScreen.components.LocalBottomBarInset
import com.yugentech.sessions.ui.dash.mainScreen.components.LocalTopBarInset
import com.yugentech.sessions.ui.dash.mainScreen.components.TopAppBar
import com.yugentech.sessions.ui.dash.mainScreen.components.smoothBottomScrimBrush
import com.yugentech.sessions.ui.dash.profileScreen.ProfileScreen
import com.yugentech.sessions.ui.dash.settingsScreen.SettingsScreen
import com.yugentech.sessions.ui.dash.settingsScreen.components.NotificationPermissionDialog
import com.yugentech.sessions.ui.dash.util.rememberNavTransitions
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private val bottomNavItems = listOf(BottomBarScreen.Profile, BottomBarScreen.Home, BottomBarScreen.Settings)

@Composable
fun MainScreen(
    userId: String,
    homeViewModel: HomeViewModel,
    timerViewModel: TimerViewModel,
    profileViewModel: ProfileViewModel,
    alertsViewModel: AlertsViewModel = koinViewModel(),
    notificationViewModel: NotificationViewModel = koinViewModel(),
    onSignOut: () -> Unit,
    onExit: () -> Unit,
    onViewInsights: () -> Unit,
    isGuest: Boolean = false,
    onAbout: () -> Unit,
    onWhatsNew: () -> Unit,
    onMoreApps: () -> Unit,
    onAppearance: () -> Unit,
    onHomeCustomization: () -> Unit = {},
    onLanguage: () -> Unit = {},
    onReplayOnboarding: () -> Unit = {},
    onNavigateRoute: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val alertsConfiguration by alertsViewModel.alertConfigurations.collectAsStateWithLifecycle()
    val shouldKeepScreenOn = alertsConfiguration.keepScreenOn && !timerState.isIdle

    DisposableEffect(view, shouldKeepScreenOn) {
        view.keepScreenOn = shouldKeepScreenOn
        onDispose {
            view.keepScreenOn = false
        }
    }

    var showNotificationPermissionDialog by rememberSaveable { mutableStateOf(false) }
    var hasCheckedPermission by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Denied: explain why it matters and point to the app's notification settings
            if (!isGranted) showNotificationPermissionDialog = true
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

    var currentScreen by rememberSaveable(
        stateSaver = androidx.compose.runtime.saveable.Saver(
            save = { it.route },
            restore = { BottomBarScreen.fromRoute(it) }
        )
    ) { mutableStateOf<BottomBarScreen>(BottomBarScreen.Home) }
    val saveableStateHolder = rememberSaveableStateHolder()

    BackHandler(enabled = true) {
        if (currentScreen != BottomBarScreen.Home) {
            currentScreen = BottomBarScreen.Home
        } else {
            showExitDialog = true
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showDeleteAllDialog by remember { mutableStateOf(false) }

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

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(stringResource(R.string.delete_all_sessions_title)) },
            text = { Text(stringResource(R.string.delete_all_sessions_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        profileViewModel.deleteAllSessions()
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(stringResource(R.string.delete_all_sessions_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(MaterialTheme.corners.large)
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                profileViewModel = profileViewModel,
                onDeleteAllSessions = { showDeleteAllDialog = true },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavBar(
                items = bottomNavItems,
                currentScreen = currentScreen,
                onSelected = { screen ->
                    currentScreen = screen
                }
            )
        }
    ) { innerPadding ->
        val transitions = rememberNavTransitions()
        // The pages run up under the transparent top bar, so their backgrounds and glows reach the
        // top of the screen; each starts its content the bar's height down
        // As on Hajime, the pages also run down under the floating nav dock, ending their content
        // just above it, so the dock floats over the page instead of sitting on a band of its own
        val bottomInset = innerPadding.calculateBottomPadding()
        CompositionLocalProvider(
            LocalTopBarInset provides innerPadding.calculateTopPadding(),
            LocalBottomBarInset provides bottomInset + DockContentGap
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        val navigatingRight =
                            bottomNavItems.indexOf(targetState) > bottomNavItems.indexOf(initialState)
                        transitions.tabSwitch(forward = navigatingRight)
                    },
                    label = "TabSwitch"
                ) { screen ->
                    saveableStateHolder.SaveableStateProvider(screen.route) {
                        when (screen) {
                            BottomBarScreen.Home -> HomeScreen(
                                userId = userId,
                                timerViewModel = timerViewModel,
                                homeViewModel = homeViewModel
                            )

                            BottomBarScreen.Profile -> ProfileScreen(
                                userId = userId,
                                onViewInsights = onViewInsights,
                                profileViewModel = profileViewModel,
                                isGuest = isGuest
                            )

                            BottomBarScreen.Settings -> SettingsScreen(
                                alertsViewModel = alertsViewModel,
                                notificationViewModel = notificationViewModel,
                                onSignOut = onSignOut,
                                isGuest = isGuest,
                                onExit = onExit,
                                onAbout = onAbout,
                                onWhatsNew = onWhatsNew,
                                onMoreApps = onMoreApps,
                                onAppearance = onAppearance,
                                onHomeCustomization = onHomeCustomization,
                                onLanguage = onLanguage,
                                onReplayOnboarding = onReplayOnboarding
                            )
                        }
                    }
                }

                // A soft fade into the background along the bottom, under the dock, so the page
                // dissolves away behind it instead of being cut off at a hard edge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(bottomInset + BottomFadeExtra)
                        .background(smoothBottomScrimBrush(MaterialTheme.colorScheme.background, bottomAlpha = 0.95f))
                )
            }
        }

        if (showNotificationPermissionDialog) {
            NotificationPermissionDialog(onDismiss = { showNotificationPermissionDialog = false })
        }

        // Centralized update announcements & dialogs (shown once per release, when timer is idle)
        AnnouncementHost(
            isTimerIdle = timerState.isIdle,
            onOpenWhatsNew = onWhatsNew,
            onNavigateRoute = onNavigateRoute
        )
    }
}

/** Space left between the end of a page's content and the top of the nav dock. */
private val DockContentGap = 8.dp

/** How far the bottom fade reaches above the dock. */
private val BottomFadeExtra = 24.dp
