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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.ui.config.screens.SettingsScreen
import com.yugentech.sessions.ui.dash.components.common.ExitConfirmationDialog
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.mainScreen.TopAppBar
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

// Items ordered: Profile - Home (Timer) - Settings
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

    // Root Container
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
            // REMOVED: bottomBar = { ... }
            // We removed it from Scaffold so the content goes all the way to the bottom
        ) { innerPadding ->

            AnimatedContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                targetState = currentScreen,
                transitionSpec = {
                    // Define the spatial order of screens
                    val screenOrder =
                        listOf(AppScreens.Profile, AppScreens.Home, AppScreens.Settings)
                    val initialIndex = screenOrder.indexOf(initialState)
                    val targetIndex = screenOrder.indexOf(targetState)

                    val animationDuration = AppConstants.DEFAULT_ANIMATION_DURATION

                    if (targetIndex > initialIndex) {
                        // Moving FORWARD (e.g. Profile -> Home)
                        // Slide in from Right, Slide out to Left
                        (slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(animationDuration)
                        ) + fadeIn(animationSpec = tween(animationDuration))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(animationDuration)
                                ) + fadeOut(animationSpec = tween(animationDuration)))
                    } else {
                        // Moving BACKWARD (e.g. Home -> Profile)
                        // Slide in from Left, Slide out to Right
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

        // FLOATING LAYER: Positioned explicitly at the bottom with high Z-Index
        ExpressiveNavigationBar(
            items = bottomNavItems,
            currentScreen = currentScreen,
            onSelected = { screen ->
                if (screen != currentScreen) {
                    currentScreen = screen
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Anchor to bottom
                .zIndex(2f) // "z alpha" -> This guarantees it floats on top of everything
        )

        ToastMessage(
            message = toastMessage,
            onDismiss = { toastMessage = null },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = MaterialTheme.spacing.xxl)
                .zIndex(3f) // Ensure toasts are even higher
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveNavigationBar(
    items: List<AppScreens>,
    currentScreen: AppScreens,
    onSelected: (AppScreens) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, screen ->
            val isSelected = currentScreen == screen

            val shape = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                items.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            }

            ToggleButton(
                checked = isSelected,
                onCheckedChange = { onSelected(screen) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .semantics { role = Role.RadioButton },
                shapes = shape,
                colors = ToggleButtonDefaults.toggleButtonColors(
                    // Unselected: surfaceContainerHighest for elevated, refined look
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    // Selected: primary colors for main navigation actions
                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = screen.selectedIcon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}