@file:Suppress("DEPRECATION")

package com.yugentech.sessions.navigation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.yugentech.sessions.LoginViewModel
import com.yugentech.sessions.sessions.SessionsViewModel
import com.yugentech.sessions.ui.screens.AboutScreen
import com.yugentech.sessions.ui.screens.LoginScreen
import com.yugentech.sessions.ui.screens.MainScreen
import com.yugentech.sessions.ui.screens.appScreens.AppearanceScreen
import com.yugentech.sessions.ui.screens.appScreens.EditProfileScreen
import com.yugentech.sessions.ui.screens.appScreens.SettingsScreen
import com.yugentech.sessions.user.UserViewModel
import com.yugentech.sessions.utils.defaultEnterTransition
import com.yugentech.sessions.utils.defaultExitTransition
import com.yugentech.sessions.utils.defaultPopEnterTransition
import com.yugentech.sessions.utils.defaultPopExitTransition

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
    sessionsViewModel: SessionsViewModel,
    webClientId: String
) {
    val authState by loginViewModel.authState.collectAsStateWithLifecycle()
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

    LaunchedEffect(authState.isUserLoggedIn) {
        if (authState.isUserLoggedIn) {
            navController.navigate(Screens.Main.route) {
                popUpTo(Screens.Login.route) { inclusive = true }
            }
        } else {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute != Screens.Login.route) {
                navController.navigate(Screens.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.Login.route,
        enterTransition = { defaultEnterTransition },
        exitTransition = { defaultExitTransition },
        popEnterTransition = { defaultPopEnterTransition },
        popExitTransition = { defaultPopExitTransition }
    ) {
        composable(Screens.Login.route) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onSignInClick = { email, password ->
                    loginViewModel.signIn(email, password)
                },
                onSignUpClick = { name, email, password ->
                    loginViewModel.signUp(name, email, password)
                },
                onGoogleSignInClick = {
                    loginViewModel.getGoogleSignInIntent(webClientId)
                }
            )
        }

        composable(Screens.Main.route) {
            authState.userId?.let { userId ->
                MainScreen(
                    userId = userId,
                    sessionsViewModel = sessionsViewModel,
                    onLogout = { loginViewModel.signOut() },
                    onEditProfile = { navController.navigate(Screens.EditProfile.route) },
                    onSettings = { navController.navigate(Screens.Settings.route) },
                    userViewModel = userViewModel
                )
            }
        }

        composable(Screens.Settings.route) {
            SettingsScreen(
                onAbout = {
                    navController.navigate(Screens.About.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAppearance = {
                    navController.navigate(Screens.Appearance.route)
                }
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
            EditProfileScreen(
                loginViewModel = loginViewModel,
                userViewModel = userViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}