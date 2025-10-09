package com.yugentech.sessions.navigation.navgraph

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yugentech.sessions.auth.state.AuthState
import com.yugentech.sessions.navigation.screen.AppScreen
import com.yugentech.sessions.ui.auth.signInScreen.SignInScreen
import com.yugentech.sessions.ui.auth.signUpScreen.SignUpScreen
import com.yugentech.sessions.ui.dash.util.defaultEnterTransition
import com.yugentech.sessions.ui.dash.util.defaultExitTransition
import com.yugentech.sessions.ui.dash.util.defaultPopEnterTransition
import com.yugentech.sessions.ui.dash.util.defaultPopExitTransition
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import timber.log.Timber

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    authState: AuthState,
    webClientId: String,
    context: android.content.Context
) {
    // Defines the sign-in screen
    composable(
        route = AppScreen.SignIn.route,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        Timber.v("Composing SignIn Screen")
        BackHandler { (context as? Activity)?.finish() }

        SignInScreen(
            authViewModel = authViewModel,
            onSignIn = { email, password ->
                authViewModel.signIn(email, password)
            },
            onGoogleSignIn = {
                authViewModel.getGoogleSignInIntent(webClientId)
            },
            onNavigateToSignUp = {
                navController.navigate(AppScreen.SignUp.route) {
                    launchSingleTop = true
                }
            },
            onForgotPassword = { email ->
                authViewModel.forgotPassword(email)
            }
        )
    }

    // Defines the sign-up screen
    composable(
        route = AppScreen.SignUp.route,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        Timber.v("Composing SignUp Screen")
        SignUpScreen(
            authViewModel = authViewModel,
            onSignUp = { name, email, password ->
                authViewModel.signUp(name, email, password)
            },
            onGoogleSignIn = {
                authViewModel.getGoogleSignInIntent(webClientId)
            },
            onNavigateToSignIn = {
                navController.popBackStack()
            }
        )
        BackHandler { navController.popBackStack() }
    }
}