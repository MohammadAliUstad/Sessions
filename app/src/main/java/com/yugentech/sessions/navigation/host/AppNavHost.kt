package com.yugentech.sessions.navigation.host

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.navigation.navgraph.authGraph
import com.yugentech.sessions.navigation.navgraph.configGraph
import com.yugentech.sessions.navigation.navgraph.dashGraph
import com.yugentech.sessions.navigation.screen.AppScreen
import com.yugentech.sessions.ui.auth.components.AuraBackgroundState
import com.yugentech.sessions.ui.auth.components.AuraRoutes
import com.yugentech.sessions.ui.auth.components.AuraTheme
import com.yugentech.sessions.ui.auth.components.LocalAuraBackground
import com.yugentech.sessions.ui.auth.components.SessionsAuraBackground
import com.yugentech.sessions.ui.dash.util.rememberNavTransitions
import timber.log.Timber

@Composable
fun AppNavHost(
    navController: NavHostController,
    webClientId: String,
    showOnboarding: Boolean,
    onOnboardingComplete: () -> Unit,
    authViewModel: AuthViewModel,
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val startDestination = remember  {
        when {
            showOnboarding -> AppScreen.Onboarding.route
            authState.isUserLoggedIn && authState.userId != null -> AppScreen.Main.route
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

    // Manages auth-driven navigation. A guest counts as logged in.
    LaunchedEffect(authState.isUserLoggedIn, authState.userId, showOnboarding) {
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

                authState.isUserLoggedIn && authState.userId != null -> {
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

    // Sign-in and sign-up sit on the one aura drawn here, as in Quill, so moving between them
    // changes only their content: the background never jumps or shows an edge, and stays still
    // while they slide over it. It fades with the switch into the app.
    val currentEntry by navController.currentBackStackEntryAsState()
    val onAura = currentEntry?.destination?.route in AuraRoutes
    val auraBackground = remember { AuraBackgroundState() }
    val transitions = rememberNavTransitions()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = onAura,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            AuraTheme {
                SessionsAuraBackground(modifier = Modifier.fillMaxSize())
            }
        }

        CompositionLocalProvider(LocalAuraBackground provides auraBackground) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { transitions.enter(this) },
                exitTransition = { transitions.exit(this) },
                popEnterTransition = { transitions.popEnter(this) },
                popExitTransition = { transitions.popExit(this) }
            ) {
                authGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    webClientId = webClientId,
                    context = context
                )
                dashGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    onOnboardingComplete = onOnboardingComplete,
                    context = context
                )
                configGraph(
                    navController = navController,
                    authState = authState
                )
            }
        }
    }
}
