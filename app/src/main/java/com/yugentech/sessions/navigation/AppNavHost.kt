@file:Suppress("DEPRECATION")

package com.yugentech.sessions.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.ui.auth.screens.SignInScreen
import com.yugentech.sessions.ui.auth.screens.SignUpScreen
import com.yugentech.sessions.ui.config.screens.AboutScreen
import com.yugentech.sessions.ui.config.screens.AppearanceScreen
import com.yugentech.sessions.ui.config.screens.EditProfileScreen
import com.yugentech.sessions.ui.config.screens.InsightsScreen
import com.yugentech.sessions.ui.config.screens.LicensesScreen
import com.yugentech.sessions.ui.dash.screens.MainScreen
import com.yugentech.sessions.ui.dash.screens.OnboardingScreen
import com.yugentech.sessions.ui.dash.utils.defaultEnterTransition
import com.yugentech.sessions.ui.dash.utils.defaultExitTransition
import com.yugentech.sessions.ui.dash.utils.defaultPopEnterTransition
import com.yugentech.sessions.ui.dash.utils.defaultPopExitTransition
import com.yugentech.sessions.ui.dash.utils.formatTime
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    showOnboarding: Boolean,
    onOnboardingComplete: () -> Unit,
    loginViewModel: LoginViewModel
) {
    val authState by loginViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val startDestination = remember {
        when {
            showOnboarding -> Screens.Onboarding.route
            authState.isUserLoggedIn && authState.userId != null -> Screens.Main.route
            else -> Screens.Main.route
        }
    }

    // Handle Google Sign-In Intent launching
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.d("Google Sign-In Activity Result OK")
                loginViewModel.handleGoogleSignInResult(result.data)
            } else {
                Timber.w("Google Sign-In Activity Result Cancelled or Failed")
            }
        }
    )

    LaunchedEffect(authState.intent) {
        authState.intent?.let {
            Timber.d("Launching Google Sign-In Intent")
            launcher.launch(IntentSenderRequest.Builder(it).build())
        }
    }

    LaunchedEffect(authState.isUserLoggedIn, authState.userId, showOnboarding) {
        if (!authState.isLoading) {
            val currentRoute = navController.currentDestination?.route

            if (showOnboarding) {
                if (currentRoute != Screens.Onboarding.route) {
                    navController.navigate(Screens.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else if (authState.isUserLoggedIn && authState.userId != null) {
                if (currentRoute != Screens.Main.route) {
                    navController.navigate(Screens.Main.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                if (currentRoute != Screens.SignIn.route && currentRoute != Screens.SignUp.route) {
                    val comingFromOnboarding = currentRoute == Screens.Onboarding.route

                    navController.navigate(Screens.SignIn.route) {
                        if (!comingFromOnboarding) {
                            popUpTo(0) { inclusive = true }
                        }

                        launchSingleTop = true
                    }
                }
            }
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        composable(
            route = Screens.Onboarding.route,
            exitTransition = { defaultExitTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            OnboardingScreen(onFinish = onOnboardingComplete)
            BackHandler { (context as? Activity)?.finish() }
        }

        composable(Screens.Licenses.route) {
            LicensesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screens.SignIn.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            Timber.v("Composing SignIn Screen")
            BackHandler { (context as? Activity)?.finish() }

            SignInScreen(
                loginViewModel = loginViewModel,
                onSignIn = { email, password ->
                    loginViewModel.signIn(email, password)
                },
                onGoogleSignIn = {
                    loginViewModel.getGoogleSignInIntent(webClientId)
                },
                onNavigateToSignUp = {
                    navController.navigate(Screens.SignUp.route)
                },
                onForgotPassword = { email ->
                    loginViewModel.forgotPassword(email)
                }
            )
        }

        composable(
            route = Screens.SignUp.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            Timber.v("Composing SignUp Screen")
            SignUpScreen(
                loginViewModel = loginViewModel,
                onSignUp = { name, email, password ->
                    loginViewModel.signUp(name, email, password)
                },
                onGoogleSignIn = {
                    loginViewModel.getGoogleSignInIntent(webClientId)
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                }
            )
            BackHandler {
                navController.popBackStack()
            }
        }

        composable(Screens.Main.route) {
            Timber.v("Composing Main Screen")
            val currentUserId = authState.userId

            val homeViewModel: HomeViewModel = koinViewModel()
            val notificationsViewModel: NotificationsViewModel = koinViewModel()
            val profileViewModel: ProfileViewModel = koinViewModel()
            val timerViewModel: TimerViewModel = koinViewModel()
            val settingsViewModel: SettingsViewModel = koinViewModel()

            if (currentUserId != null) {
                MainScreen(
                    userId = currentUserId,
                    onSignOut = {
                        Timber.i("User requested Sign Out")
                        timerViewModel.stopAndDiscardSession()
                        notificationsViewModel.cancelReminders()
                        loginViewModel.signOut()
                    },
                    onExit = {
                        Timber.i("User requested App Exit")
                        timerViewModel.stopAndDiscardSession()
                        (context as? Activity)?.finish()
                    },
                    onEditProfile = {
                        navController.navigate(Screens.EditProfile.route)
                    },
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    timerViewModel = timerViewModel,
                    settingsViewModel = settingsViewModel,
                    notificationsViewModel = notificationsViewModel,
                    onAbout = {
                        navController.navigate(Screens.About.route)
                    },
                    onAppearance = {
                        navController.navigate(Screens.Appearance.route)
                    },
                    onViewInsights = {
                        navController.navigate(Screens.Insights.route)
                    }
                )
            }
        }

        composable(Screens.Appearance.route) {
            val themeViewModel: ThemeViewModel = koinViewModel()
            AppearanceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                themeViewModel = themeViewModel
            )
        }

        composable(Screens.Insights.route) { it ->
            // 1. Inject the merged ProfileViewModel instead of InsightsViewModel
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screens.Main.route)
            }
            val profileViewModel: ProfileViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)
            // 2. Collect the consolidated UI state
            val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
            val currentUserId = authState.userId

            // 3. Ensure data is loaded if it hasn't been already
            LaunchedEffect(currentUserId) {
                currentUserId?.let { profileViewModel.loadProfile(it) }
            }

            InsightsScreen(
                // Pass the calculated total time from the merged state
                totalTime = formatTime(profileUiState.totalTime),
                // Pass the task distribution for your charts
                taskDistribution = profileUiState.taskDistribution,
                onBack = { navController.popBackStack() },
                streakCount = profileUiState.streakCount,
                dailyVolume = profileUiState.dailyVolume,
                peakHour = profileUiState.peakHour,
                heatmapHistory = profileUiState.heatmapHistory,
            )
        }

        composable(Screens.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLicenses = {
                    navController.navigate(Screens.Licenses.route)
                }
            )
        }

        composable(Screens.EditProfile.route) {
            Timber.v("Composing EditProfile Screen")
            val currentUserId = authState.userId
            val profileViewModel: ProfileViewModel = koinViewModel()

            if (currentUserId != null) {
                EditProfileScreen(
                    profileViewModel = profileViewModel,
                    userId = currentUserId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                Timber.w("Navigated to EditProfile without valid User ID")
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}