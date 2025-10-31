package com.yugentech.sessions.ui.components.signInUp

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.yugentech.sessions.R
import com.yugentech.sessions.ui.components
import com.yugentech.sessions.ui.corners
import com.yugentech.sessions.ui.icons
import com.yugentech.sessions.ui.spacing

@Composable
fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.buttonHeight),
        shape = RoundedCornerShape(MaterialTheme.corners.medium),
        enabled = !isLoading,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MaterialTheme.icons.small)
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))
        Text(
            text = "Continue with Google",
            style = MaterialTheme.typography.titleMedium
        )
    }
}