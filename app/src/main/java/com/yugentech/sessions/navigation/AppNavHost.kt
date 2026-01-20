@file:Suppress("DEPRECATION")

package com.yugentech.sessions.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.ui.auth.screens.SignInScreen
import com.yugentech.sessions.ui.auth.screens.SignUpScreen
import com.yugentech.sessions.ui.config.screens.AboutScreen
import com.yugentech.sessions.ui.config.screens.AppearanceScreen
import com.yugentech.sessions.ui.config.screens.LicensesScreen
import com.yugentech.sessions.ui.dash.screens.EditProfileScreen
import com.yugentech.sessions.ui.dash.screens.ExpressiveOnboardingScreen
import com.yugentech.sessions.ui.dash.screens.MainScreen
import com.yugentech.sessions.utils.defaultEnterTransition
import com.yugentech.sessions.utils.defaultExitTransition
import com.yugentech.sessions.utils.defaultPopEnterTransition
import com.yugentech.sessions.utils.defaultPopExitTransition
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

// Define a longer duration for that "Premium/Smooth" feel
private const val SMOOTH_TRANSITION_DURATION = 400

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    showOnboarding: Boolean,
    onOnboardingComplete: () -> Unit,
    loginViewModel: LoginViewModel
) {
    // Collect Auth State to drive navigation logic
    val authState by loginViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 1. FREEZE START DESTINATION
    // We remember this so the graph doesn't rebuild/snap when flags change
    val startDestination = remember {
        when {
            showOnboarding -> Screens.Onboarding.route
            authState.isUserLoggedIn && authState.userId != null -> Screens.Main.route
            else -> Screens.SignIn.route
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

    // Trigger Google Sign-In Launcher when Intent is ready
    LaunchedEffect(authState.intent) {
        authState.intent?.let {
            Timber.d("Launching Google Sign-In Intent")
            launcher.launch(IntentSenderRequest.Builder(it).build())
        }
    }

    // -------------------------------------------------------------------------
    // CENTRAL NAVIGATION LOGIC (The "Brain")
    // -------------------------------------------------------------------------
    LaunchedEffect(authState.isUserLoggedIn, authState.userId, showOnboarding) {
        if (!authState.isLoading) {
            val currentRoute = navController.currentDestination?.route

            // PRIORITY 1: ONBOARDING
            if (showOnboarding) {
                if (currentRoute != Screens.Onboarding.route) {
                    navController.navigate(Screens.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            // PRIORITY 2: MAIN APP (Logged In)
            else if (authState.isUserLoggedIn && authState.userId != null) {
                if (currentRoute != Screens.Main.route) {
                    navController.navigate(Screens.Main.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            // PRIORITY 3: SIGN IN (Not Logged In)
            else {
                if (currentRoute != Screens.SignIn.route && currentRoute != Screens.SignUp.route) {
                    // Check if we are animating FROM Onboarding
                    val comingFromOnboarding = currentRoute == Screens.Onboarding.route

                    navController.navigate(Screens.SignIn.route) {
                        // FIX: Do NOT pop the stack if coming from Onboarding.
                        // Popping immediately kills the exit animation.
                        // We let it slide normally, and the BackHandler in SignIn prevents going back.
                        if (!comingFromOnboarding) {
                            popUpTo(0) { inclusive = true }
                        }

                        launchSingleTop = true
                    }
                }
            }
        }
    }

    // Show loading spinner
    if (authState.isLoading && navController.currentDestination?.route == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { defaultEnterTransition(AppConstants.DEFAULT_ANIMATION_DURATION) },
        exitTransition = { defaultExitTransition(AppConstants.DEFAULT_ANIMATION_DURATION) },
        popEnterTransition = { defaultPopEnterTransition(AppConstants.DEFAULT_ANIMATION_DURATION) },
        popExitTransition = { defaultPopExitTransition(AppConstants.DEFAULT_ANIMATION_DURATION) }
    ) {

        // --- ROUTE: ONBOARDING ---
        // Ensure exit transition is long enough to see
        composable(
            route = Screens.Onboarding.route,
            exitTransition = { defaultExitTransition(SMOOTH_TRANSITION_DURATION) },
            popExitTransition = { defaultPopExitTransition(SMOOTH_TRANSITION_DURATION) }
        ) {
            ExpressiveOnboardingScreen(onFinish = onOnboardingComplete)
            // Prevent backing out of the app from onboarding (optional)
            BackHandler { (context as? Activity)?.finish() }
        }

        composable(Screens.Licenses.route) {
            LicensesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- ROUTE: SIGN IN ---
        composable(
            route = Screens.SignIn.route,
            enterTransition = { defaultEnterTransition(SMOOTH_TRANSITION_DURATION) },
            exitTransition = { defaultExitTransition(SMOOTH_TRANSITION_DURATION) },
            popEnterTransition = { defaultPopEnterTransition(SMOOTH_TRANSITION_DURATION) },
            popExitTransition = { defaultPopExitTransition(SMOOTH_TRANSITION_DURATION) }
        ) {
            Timber.v("Composing SignIn Screen")
            // This BackHandler ensures that if they came from Onboarding,
            // pressing back here exits the app instead of returning to the finished Onboarding.
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

        // --- ROUTE: SIGN UP ---
        composable(
            route = Screens.SignUp.route,
            enterTransition = { defaultEnterTransition(SMOOTH_TRANSITION_DURATION) },
            exitTransition = { defaultExitTransition(SMOOTH_TRANSITION_DURATION) },
            popEnterTransition = { defaultPopEnterTransition(SMOOTH_TRANSITION_DURATION) },
            popExitTransition = { defaultPopExitTransition(SMOOTH_TRANSITION_DURATION) }
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

        // --- ROUTE: MAIN (DASHBOARD) ---
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
                    }
                )
            }
        }

        // --- ROUTE: SETTINGS & SUB-SCREENS ---
        composable(Screens.Appearance.route) {
            val themeViewModel: ThemeViewModel = koinViewModel()
            AppearanceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                themeViewModel = themeViewModel
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