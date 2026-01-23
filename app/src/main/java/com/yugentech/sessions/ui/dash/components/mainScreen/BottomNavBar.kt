package com.yugentech.sessions.ui.dash.components.mainScreen

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomNavBar(
    items: List<AppScreens>,
    currentScreen: AppScreens,
    onSelected: (AppScreens) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                start = MaterialTheme.spacing.sm,
                end = MaterialTheme.spacing.sm,
                top = MaterialTheme.spacing.sm,
                bottom = MaterialTheme.spacing.z
            ),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, screen ->
            val isSelected = currentScreen == screen

            val shape = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                items.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            }

            ToggleButton(
                checked = isSelected,
                onCheckedChange = { onSelected(screen) },
                modifier = Modifier
                    .weight(1f)
                    .height(MaterialTheme.components.buttonLarge)
                    .semantics { role = Role.RadioButton },
                shapes = shape,
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = screen.selectedIcon,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.icons.medium)
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        repeatDelayMillis = AppAnimations.Durations.RepeatDelay,
                        initialDelayMillis = AppAnimations.Durations.InitialDelay,
                        velocity = AppAnimations.Motion.Velocity
                    )
                )
            }
        }
    }
}