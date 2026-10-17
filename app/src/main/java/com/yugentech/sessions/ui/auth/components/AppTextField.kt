package com.yugentech.sessions.ui.auth.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.utils.AppConstants

/** Hajime's AppTextField, line for line. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    error: String = AppConstants.EMPTY,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Hajime's theme uses standard motion; Sessions' uses expressive, which makes the label and
        // container spring as focus moves. Standard here, so the field moves as Hajime's does.
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            motionScheme = MotionScheme.standard()
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = label) },
                isError = error.isNotEmpty(),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent, // The error text below says it; no underline on a pill.
                    errorContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                leadingIcon = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.icons.medium)
                    )
                },
                trailingIcon = if (isPassword) {
                    {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(MaterialTheme.components.buttonSmall)
                        ) {
                            AnimatedEyeIcon(
                                isVisible = passwordVisible,
                                modifier = Modifier.size(MaterialTheme.icons.medium)
                            )
                        }
                    }
                } else null,
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None
            )
        }

        ErrorText(error = error)
    }
}

@Composable
private fun ErrorText(
    error: String
) {
    AnimatedVisibility(
        visible = error.isNotEmpty(),
        enter = expandVertically(tween(AppAnimations.Durations.Fast)) +
                fadeIn(tween(AppAnimations.Durations.Fast)),
        exit = shrinkVertically(tween(AppAnimations.Durations.Fast)) +
                fadeOut(tween(AppAnimations.Durations.Fast))
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.m, // In from the pill's rounded end.
                top = MaterialTheme.spacing.xs
            )
        )
    }
}
