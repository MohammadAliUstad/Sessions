package com.yugentech.sessions.navigation.host

import android.app.Activity
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
import androidx.navigation.compose.NavHost
import com.yugentech.sessions.navigation.navgraph.authGraph
import com.yugentech.sessions.navigation.navgraph.configGraph
import com.yugentech.sessions.navigation.navgraph.dashGraph
import com.yugentech.sessions.navigation.screen.AppScreen
import com.yugentech.sessions.ui.dash.util.defaultEnterTransition
import com.yugentech.sessions.ui.dash.util.defaultExitTransition
import com.yugentech.sessions.ui.dash.util.defaultPopEnterTransition
import com.yugentech.sessions.ui.dash.util.defaultPopExitTransition
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    showOnboarding: Boolean,
    onOnboardingComplete: () -> Unit,
    authViewModel: AuthViewModel,
    shouldNavigateToHome: Boolean = false,
    onNavigatedToHome: () -> Unit = {}
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val startDestination = remember  {
        when {
            showOnboarding -> AppScreen.Onboarding.route
            (authState.isUserLoggedIn && authState.userId != null) || authState.isGuest -> AppScreen.Main.route
            else -> AppScreen.SignIn.route
        }
    }

    // Handles the Google Sign-In intent launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.d("Google Sign-In Activity Result OK")
                authViewModel.handleGoogleSignInResult(result.data)
            } else {
                Timber.w("Google Sign-In Activity Result Cancelled or Failed")
            }
        }
    )

    // Launches the Google Sign-In intent when available
    LaunchedEffect(authState.intent) {
        authState.intent?.let {
            Timber.d("Launching Google Sign-In Intent")
            launcher.launch(IntentSenderRequest.Builder(it).build())
        }
    }

    // Manages auth-driven navigation
    LaunchedEffect(authState.isUserLoggedIn, authState.userId, authState.isGuest, showOnboarding) {
        if (!authState.isLoading && !authState.isInitializing) {
            val currentRoute = navController.currentDestination?.route

            when {
                showOnboarding -> {
                    if (currentRoute != AppScreen.Onboarding.route) {
                        navController.navigate(AppScreen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                (authState.isUserLoggedIn && authState.userId != null) || authState.isGuest -> {
                    if (currentRoute != AppScreen.Main.route) {
                        navController.navigate(AppScreen.Main.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                else -> {
                    if (currentRoute != AppScreen.SignIn.route && currentRoute != AppScreen.SignUp.route) {
                        navController.navigate(AppScreen.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    // Forces navigation back to main when requested
    LaunchedEffect(shouldNavigateToHome) {
        if (shouldNavigateToHome) {
            Timber.d("Navigating to Main screen")
            navController.navigate(AppScreen.Main.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            onNavigatedToHome()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        authGraph(
            navController = navController,
            authViewModel = authViewModel,
            authState = authState,
            webClientId = webClientId,
            context = context
        )
        dashGraph(
            navController = navController,
            authViewModel = authViewModel,
            authState = authState,
            onOnboardingComplete = onOnboardingComplete,
            context = context
        )
        configGraph(
            navController = navController,
            authState = authState,
            authViewModel = authViewModel
        )
    }
}