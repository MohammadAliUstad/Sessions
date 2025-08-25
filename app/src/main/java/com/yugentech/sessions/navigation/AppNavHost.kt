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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
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
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel
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

    val startDestination = if (authState.isUserLoggedIn && authState.userId != null) {
        Screens.Main.route
    } else if (authState.isLoading) {
        return
    } else {
        Screens.Login.route
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { defaultEnterTransition },
        exitTransition = { defaultExitTransition },
        popEnterTransition = { defaultPopEnterTransition },
        popExitTransition = { defaultPopExitTransition }
    ) {
        composable(Screens.Login.route) {
            BackHandler {
                (context as? Activity)?.finish()
            }

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
            val currentUserId = authState.userId
            if (currentUserId != null) {
                MainScreen(
                    userId = currentUserId,
                    onLogout = {
                        loginViewModel.signOut()
                        navController.navigate(Screens.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onEditProfile = {
                        navController.navigate(Screens.EditProfile.route)
                    },
                    onSettings = {
                        navController.navigate(Screens.Settings.route)
                    },
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    userViewModel = userViewModel
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
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