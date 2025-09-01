package com.yugentech.sessions.ui.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.auth.components.forms.SignUpForm
import com.yugentech.sessions.ui.dash.mainScreen.components.ToastMessage
import com.yugentech.sessions.auth.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onSignUp: (name: String, email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(MaterialTheme.spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))

                Box(
                    modifier = Modifier
                        .size(MaterialTheme.components.imageSizeMedium)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.components.imageSizeSmall),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                Text(
                    text = stringResource(R.string.sign_up_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

                SignUpForm(
                    isLoading = authState.isLoading,
                    onSignUp = onSignUp,
                    onGoogleSignIn = onGoogleSignIn,
                    onClearError = { authViewModel.clearError() }
                )

                TextButton(
                    onClick = onNavigateToSignIn,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.xs)
                ) {
                    Text(
                        text = stringResource(R.string.have_an_account),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))
            }

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
}