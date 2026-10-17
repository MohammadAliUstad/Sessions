package com.yugentech.sessions.ui.auth.signUpScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.AnimatedAlarmIcon
import com.yugentech.sessions.ui.auth.components.AuthScaffold
import com.yugentech.sessions.ui.auth.components.AuthSwitchLink
import com.yugentech.sessions.ui.auth.components.forms.SignUpForm
import com.yugentech.sessions.ui.dash.mainScreen.components.ToastMessage
import kotlinx.coroutines.delay

/** Start your productivity journey, under the ringing alarm: laid out as Hajime's sign-up. */
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onSignUp: (name: String, email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    var isIconActivated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500) // Small delay to ensure view is ready
        isIconActivated = true
    }

    AuthScaffold(
        title = "Sessions",
        subtitle = "Start your productivity journey",
        icon = {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedAlarmIcon(
                    isActivated = isIconActivated,
                    modifier = Modifier.size(MaterialTheme.components.imageSizeSmall)
                )
            }
        },
        overlay = {
            Box(modifier = Modifier.fillMaxSize()) {
                ToastMessage(
                    message = authState.error,
                    onDismiss = { authViewModel.clearError() },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .zIndex(1f)
                )
            }
        }
    ) {
        SignUpForm(
            isLoading = authState.isLoading,
            onSignUp = onSignUp,
            onGoogleSignIn = onGoogleSignIn,
            onClearError = { authViewModel.clearError() }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

        AuthSwitchLink(
            prompt = "Already focusing with us?",
            action = "Sign in",
            onClick = onNavigateToSignIn
        )
    }
}
