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
import com.yugentech.sessions.ui.screens.AboutScreen
import com.yugentech.sessions.ui.screens.MainScreen
import com.yugentech.sessions.ui.screens.SignInScreen
import com.yugentech.sessions.ui.screens.SignUpScreen
import com.yugentech.sessions.ui.screens.appScreens.AppearanceScreen
import com.yugentech.sessions.ui.screens.appScreens.EditProfileScreen
import com.yugentech.sessions.ui.screens.appScreens.SettingsScreen
import com.yugentech.sessions.user.UserViewModel
import com.yugentech.sessions.utils.defaultEnterTransition
import com.yugentech.sessions.utils.defaultExitTransition
import com.yugentech.sessions.utils.defaultPopEnterTransition
import com.yugentech.sessions.utils.defaultPopExitTransition
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    notificationsViewModel: NotificationsViewModel
) {
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
        enterTransition = { defaultEnterTransition },
        exitTransition = { defaultExitTransition },
        popEnterTransition = { defaultPopEnterTransition },
        popExitTransition = { defaultPopExitTransition }
    ) {
        composable(Screens.SignIn.route) {
            BackHandler {
                (context as? Activity)?.finish()
            }

            SignInScreen(
                loginViewModel = loginViewModel,
                onSignInClick = { email, password ->
                    loginViewModel.signIn(email, password)
                },
                onGoogleSignInClick = {
                    loginViewModel.getGoogleSignInIntent(webClientId)
                },
                onNavigateToSignUp = {
                    navController.navigate(Screens.SignUp.route)
                }
            )
        }

        composable(Screens.SignUp.route) {
            SignUpScreen(
                loginViewModel = loginViewModel,
                onSignUpClick = { name, email, password ->
                    loginViewModel.signUp(name, email, password)
                },
                onGoogleSignInClick = {
                    loginViewModel.getGoogleSignInIntent(webClientId)
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
            )
            BackHandler {
                navController.popBackStack()
            }
        }

        composable(Screens.Main.route) {
            val currentUserId = authState.userId
            if (currentUserId != null) {
                MainScreen(
                    userId = currentUserId,
                    onLogout = {
                        loginViewModel.signOut()
                    },
                    onEditProfile = {
                        navController.navigate(Screens.EditProfile.route)
                    },
                    onSettings = {
                        navController.navigate(Screens.Settings.route)
                    },
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    userViewModel = userViewModel,
                    notificationsViewModel = notificationsViewModel
                )
            }
        }

        composable(Screens.Settings.route) {
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
            if (currentUserId != null) {
                EditProfileScreen(
                    userViewModel = userViewModel,
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