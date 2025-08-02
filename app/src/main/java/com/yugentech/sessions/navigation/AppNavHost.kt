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
import com.yugentech.sessions.authentication.AuthViewModel
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.ui.screens.AboutScreen
import com.yugentech.sessions.ui.screens.LoginScreen
import com.yugentech.sessions.ui.screens.MainScreen
import com.yugentech.sessions.ui.screens.appScreens.EditProfileScreen
import com.yugentech.sessions.ui.screens.appScreens.SettingsScreen
import com.yugentech.sessions.utils.defaultEnterTransition
import com.yugentech.sessions.utils.defaultExitTransition
import com.yugentech.sessions.utils.defaultPopEnterTransition
import com.yugentech.sessions.utils.defaultPopExitTransition

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    sessionViewModel: SessionViewModel,
    webClientId: String
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                authViewModel.handleGoogleSignInResult(result.data)
            }
        }
    )

    LaunchedEffect(authState.pendingIntent) {
        authState.pendingIntent?.let {
            launcher.launch(IntentSenderRequest.Builder(it).build())
        }
    }

    val startDestination = if (authState.isUserLoggedIn) Screens.Main.route else Screens.Login.route

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { defaultEnterTransition },
        exitTransition = { defaultExitTransition },
        popEnterTransition = { defaultPopEnterTransition },
        popExitTransition = { defaultPopExitTransition }
    ) {
        composable(Screens.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onSignInClick = { email, password -> authViewModel.signIn(email, password) },
                onSignUpClick = { name, email, password ->
                    authViewModel.signUp(
                        name,
                        email,
                        password
                    )
                },
                onGoogleSignInClick = { authViewModel.getGoogleSignInIntent(webClientId) }
            )
        }

        composable(Screens.Main.route) {
            val userId = authState.userId
            if (userId != null) {
                MainScreen(
                    userId = userId,
                    authViewModel = authViewModel,
                    sessionViewModel = sessionViewModel,
                    onLogout = { authViewModel.signOut() },
                    onEditProfile = { navController.navigate(Screens.EditProfile.route) },
                    onSettings = { navController.navigate(Screens.Settings.route) }
                )
            }
        }

        composable(Screens.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAbout = {
                    navController.navigate(Screens.About.route)
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
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}