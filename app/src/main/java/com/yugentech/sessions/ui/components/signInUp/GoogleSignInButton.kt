package com.yugentech.sessions.ui.components.signInUp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.R
import com.yugentech.sessions.ui.AppTokens

@Composable
fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val tokens = AppTokens.current()

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(tokens.components.buttonHeight),
        shape = RoundedCornerShape(tokens.corners.medium),
        enabled = !isLoading,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(tokens.components.iconSmall)
        )
        Spacer(modifier = Modifier.width(tokens.spacing.s))
        Text(
            text = "Continue with Google",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = tokens.typography.label.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}