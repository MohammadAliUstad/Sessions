package com.yugentech.sessions.ui.dash.mainScreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.yugentech.sessions.navigation.screen.BottomBarScreen
import com.yugentech.sessions.notification.viewmodel.NotificationsViewModel
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.ui.dash.homeScreen.components.ExitConfirmationDialog
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import com.yugentech.sessions.ui.dash.homeScreen.HomeScreen
import com.yugentech.sessions.ui.dash.mainScreen.components.BottomNavBar
import com.yugentech.sessions.ui.dash.mainScreen.components.ToastMessage
import com.yugentech.sessions.ui.dash.mainScreen.components.TopAppBar
import com.yugentech.sessions.ui.dash.profileScreen.ProfileScreen
import com.yugentech.sessions.ui.dash.settingsScreen.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val bottomNavItems = listOf(BottomBarScreen.Profile, BottomBarScreen.Home, BottomBarScreen.Settings)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: String,
    homeViewModel: HomeViewModel,
    timerViewModel: TimerViewModel,
    profileViewModel: ProfileViewModel,
    alertsViewModel: AlertsViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    onSignOut: () -> Unit,
    onExit: () -> Unit,
    onEditProfile: () -> Unit,
    onViewInsights: () -> Unit,
    onAbout: () -> Unit,
    onWhatsNew: () -> Unit,
    onAppearance: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var hasCheckedPermission by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

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

    val homeIndex = bottomNavItems.indexOf(BottomBarScreen.Home)

    val pagerState = rememberPagerState(
        initialPage = homeIndex,
        pageCount = { bottomNavItems.size }
    )

    val currentScreen by remember {
        derivedStateOf { bottomNavItems[pagerState.targetPage] }
    }

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
            title = { Text("Delete All Sessions?") },
            text = { Text("This will permanently remove all your focus history. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.deleteAllSessions()
                        alertsViewModel.performHaptic(view)
                        showDeleteAllDialog = false
                    }
                ) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel")
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
                onLogout = {},
                onSettings = {
                    scope.launch {
                        pagerState.animateScrollToPage(bottomNavItems.indexOf(BottomBarScreen.Settings))
                    }
                },
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
                    scope.launch {
                        pagerState.animateScrollToPage(bottomNavItems.indexOf(screen))
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            when (bottomNavItems[page]) {
                BottomBarScreen.Home -> HomeScreen(
                    userId = userId,
                    timerViewModel = timerViewModel,
                    homeViewModel = homeViewModel
                )

                BottomBarScreen.Profile -> ProfileScreen(
                    userId = userId,
                    onEditProfile = onEditProfile,
                    onViewInsights = onViewInsights,
                    profileViewModel = profileViewModel
                )

                BottomBarScreen.Settings -> SettingsScreen(
                    alertsViewModel = alertsViewModel,
                    notificationsViewModel = notificationsViewModel,
                    onSignOut = onSignOut,
                    onExit = onExit,
                    onAbout = onAbout,
                    onWhatsNew = onWhatsNew,
                    onAppearance = onAppearance
                )
            }
        }

        if (toastMessage != null) {
            ToastMessage(
                message = toastMessage,
                onDismiss = { toastMessage = null },
                modifier = Modifier
                    .zIndex(3f)
            )
        }
    }
}