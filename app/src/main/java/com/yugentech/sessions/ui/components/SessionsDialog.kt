package com.yugentech.sessions.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

/** A quieter action under the main button: a full-width text button, in [color] if given. */
data class DialogAction(
    val label: String,
    val onClick: () -> Unit,
    val color: Color? = null
)

/**
 * The app's dialog, laid out like the announcement and review dialogs: an icon, a bold title and
 * a message, centred, then one large pill-shaped main button with any quieter actions as
 * full-width text buttons beneath it. [destructive] turns the icon and main button to the error
 * colour, for deleting things. [content] goes between the message and the buttons.
 * [secondaryActionsInRow] puts the quieter actions side by side instead of stacked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsDialog(
    title: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    message: String? = null,
    destructive: Boolean = false,
    secondaryActions: List<DialogAction> = emptyList(),
    secondaryActionsInRow: Boolean = false,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val accent = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val onAccent = if (destructive) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.86f)
    ) {
        Card(
            shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.l)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                message?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s)
                    )
                }

                content?.invoke(this)

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                ) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            onPrimary()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.components.buttonLarge),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = onAccent)
                    ) {
                        Text(
                            text = primaryLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    @Composable
                    fun SecondaryButton(action: DialogAction, modifier: Modifier) {
                        TextButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                action.onClick()
                            },
                            modifier = modifier,
                            shape = RoundedCornerShape(MaterialTheme.corners.medium)
                        ) {
                            Text(
                                text = action.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = action.color ?: MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (secondaryActionsInRow) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                        ) {
                            secondaryActions.forEach { SecondaryButton(it, Modifier.weight(1f)) }
                        }
                    } else {
                        secondaryActions.forEach { SecondaryButton(it, Modifier.fillMaxWidth()) }
                    }
                }
            }
        }
    }
}

/** A highlighted line inside a [SessionsDialog]: the setting to change, or what's being deleted. */
@Composable
fun DialogHighlight(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(MaterialTheme.corners.medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.m,
                vertical = MaterialTheme.spacing.s
            )
        )
    }
}
