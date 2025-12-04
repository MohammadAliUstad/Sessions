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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.ui.auth.screens.SignInScreen
import com.yugentech.sessions.ui.auth.screens.SignUpScreen
import com.yugentech.sessions.ui.config.screens.AboutScreen
import com.yugentech.sessions.ui.config.screens.AppearanceScreen
import com.yugentech.sessions.ui.config.screens.SettingsScreen
import com.yugentech.sessions.ui.dash.screens.EditProfileScreen
import com.yugentech.sessions.ui.dash.screens.MainScreen
import com.yugentech.sessions.utils.Constants.DEFAULT_ANIMATION_DURATION
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    loginViewModel: LoginViewModel
) {
    // Collect Auth State to drive navigation logic
    val authState by loginViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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

    // Main Navigation Logic: Route to Main or SignIn based on Auth State
    LaunchedEffect(authState.isUserLoggedIn, authState.userId) {
        if (!authState.isLoading) {
            val currentRoute = navController.currentDestination?.route

            if (authState.isUserLoggedIn && authState.userId != null) {
                if (currentRoute != Screens.Main.route) {
                    Timber.i("User authenticated, navigating to Main Screen")
                    navController.navigate(Screens.Main.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                if (currentRoute != Screens.SignIn.route && currentRoute != Screens.SignUp.route) {
                    Timber.i("User not authenticated, navigating to Sign In Screen")
                    navController.navigate(Screens.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    // Show loading spinner while determining auth state
    if (authState.isLoading && navController.currentDestination?.route == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (authState.isUserLoggedIn && authState.userId != null) {
        Screens.Main.route
    } else {
        Screens.SignIn.route
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { defaultEnterTransition(DEFAULT_ANIMATION_DURATION) },
        exitTransition = { defaultExitTransition(DEFAULT_ANIMATION_DURATION) },
        popEnterTransition = { defaultPopEnterTransition(DEFAULT_ANIMATION_DURATION) },
        popExitTransition = { defaultPopExitTransition(DEFAULT_ANIMATION_DURATION) }
    ) {
        composable(Screens.SignIn.route) {
            Timber.v("Composing SignIn Screen")
            BackHandler {
                (context as? Activity)?.finish()
            }

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

        composable(Screens.SignUp.route) {
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

            // ViewModels scoped to Main Screen flow
            val homeViewModel: HomeViewModel = koinViewModel()
            val notificationsViewModel: NotificationsViewModel = koinViewModel()
            val profileViewModel: ProfileViewModel = koinViewModel()

            if (currentUserId != null) {
                MainScreen(
                    userId = currentUserId,
                    onSignOut = {
                        Timber.i("User requested Sign Out")
                        homeViewModel.resetSessionState()
                        notificationsViewModel.stopActiveSession()
                        notificationsViewModel.cancelReminders()
                        loginViewModel.signOut()
                    },
                    onExit = {
                        Timber.i("User requested App Exit")
                        homeViewModel.resetSessionState()
                        notificationsViewModel.stopActiveSession()
                        (context as? Activity)?.finish()
                    },
                    onEditProfile = {
                        navController.navigate(Screens.EditProfile.route)
                    },
                    onSettings = {
                        navController.navigate(Screens.Settings.route)
                    },
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    notificationsViewModel = notificationsViewModel
                )
            }
        }

        composable(Screens.Settings.route) {
            Timber.v("Composing Settings Screen")
            val settingsViewModel: SettingsViewModel = koinViewModel()
            val notificationsViewModel: NotificationsViewModel = koinViewModel()

            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onAbout = {
                    navController.navigate(Screens.About.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAppearance = {
                    navController.navigate(Screens.Appearance.route)
                },
                notificationsViewModel = notificationsViewModel
            )
        }

        composable(Screens.Appearance.route) {
            AppearanceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screens.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
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