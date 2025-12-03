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
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
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
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    loginViewModel: LoginViewModel
) {
    val notificationsViewModel: NotificationsViewModel = koinViewModel()


    val authState by loginViewModel.authState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loginViewModel.handleGoogleSignInResult(result.data)
            }
        }
    )

    LaunchedEffect(authState.intent) {
        authState.intent?.let {
            launcher.launch(IntentSenderRequest.Builder(it).build())
        }
    }

    LaunchedEffect(authState.isUserLoggedIn, authState.userId) {
        if (!authState.isLoading) {
            val currentRoute = navController.currentDestination?.route

            if (authState.isUserLoggedIn && authState.userId != null) {
                if (currentRoute != Screens.Main.route) {
                    navController.navigate(Screens.Main.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                if (currentRoute != Screens.SignIn.route && currentRoute != Screens.SignUp.route) {
                    navController.navigate(Screens.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

//    LaunchedEffect(Unit) {
//        notificationsViewModel.startActiveSession(
//            notification = Notification(
//                id = 1001,
//                title = "Initializing...",
//                message = "Starting service",
//                type = NotificationType.ACTIVE,
//                isOngoing = true,
//                remainingSeconds = 1
//            )
//        )
//
//        delay(100)
//        notificationsViewModel.stopActiveSession()
//    }

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
            val currentUserId = authState.userId
            val homeViewModel: HomeViewModel = koinViewModel()
            val notificationsViewModel: NotificationsViewModel = koinViewModel()
            val profileViewModel: ProfileViewModel = koinViewModel()


            if (currentUserId != null) {
                MainScreen(
                    userId = currentUserId,
                    onSignOut = {
                        homeViewModel.resetSessionState()
                        notificationsViewModel.stopActiveSession()
                        notificationsViewModel.cancelReminders()
                        loginViewModel.signOut()
                    },
                    onExit = {
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
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}