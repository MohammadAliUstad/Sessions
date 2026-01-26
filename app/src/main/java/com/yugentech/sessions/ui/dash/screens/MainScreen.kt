package com.yugentech.sessions.ui.dash.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yugentech.sessions.theme.tokens.components
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// Order matters here: 0 = Profile, 1 = Home, 2 = Settings
private val bottomNavItems = listOf(AppScreens.Profile, AppScreens.Home, AppScreens.Settings)

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
    onViewInsights: () -> Unit,
    onAbout: () -> Unit,
    onAppearance: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var hasCheckedPermission by rememberSaveable { mutableStateOf(false) }

    // Setup Notification Permission
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

    // --- Pager & Navigation State ---

    // Initial index is 1 because Home is the middle item in the list
    val homeIndex = bottomNavItems.indexOf(AppScreens.Home)

    val pagerState = rememberPagerState(
        initialPage = homeIndex,
        pageCount = { bottomNavItems.size }
    )

    // Derived state so TopBar/BottomBar update automatically when we swipe
    val currentScreen by remember {
        derivedStateOf { bottomNavItems[pagerState.currentPage] }
    }

    var showExitDialog by remember { mutableStateOf(false) }

    // Handle Back Press: If not on Home, swipe to Home. If on Home, show Exit Dialog.
    BackHandler(enabled = true) {
        if (pagerState.currentPage != homeIndex) {
            scope.launch {
                pagerState.animateScrollToPage(homeIndex)
            }
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
                    onSettings = {
                        // Settings is the last item
                        scope.launch {
                            pagerState.animateScrollToPage(bottomNavItems.indexOf(AppScreens.Settings))
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->

            // Replaces AnimatedContent with HorizontalPager for swipe gestures
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                // Keep the pages nearby in memory for smoother swiping
                beyondViewportPageCount = 1
            ) { page ->
                when (bottomNavItems[page]) {
                    AppScreens.Home -> HomeScreen(
                        userId = userId,
                        timerViewModel = timerViewModel,
                        homeViewModel = homeViewModel
                    )

                    AppScreens.Profile -> ProfileScreen(
                        userId = userId,
                        onEditProfile = onEditProfile,
                        onViewInsights = onViewInsights,
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
                scope.launch {
                    pagerState.animateScrollToPage(bottomNavItems.indexOf(screen))
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
                .align(Alignment.TopCenter)
                .zIndex(3f)
        )
    }
}