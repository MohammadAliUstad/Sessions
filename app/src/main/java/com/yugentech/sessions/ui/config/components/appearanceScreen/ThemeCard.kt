package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThemeCard(
    themeOption: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ToggleButton(
        checked = isSelected,
        onCheckedChange = { onClick() },
        modifier = Modifier.fillMaxWidth(),
        shapes = ToggleButtonShapes(
            shape = ToggleButtonDefaults.squareShape,
            pressedShape = ToggleButtonDefaults.pressedShape,
            checkedShape = ToggleButtonDefaults.roundShape
        ),
        colors = ToggleButtonDefaults.toggleButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.s),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.icons.large)
                    .clip(CircleShape)
                    .background(brush = Brush.linearGradient(themeOption.gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Surface(
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.selected),
                            tint = themeOption.primaryColor,
                            modifier = Modifier.padding(MaterialTheme.spacing.xxs)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

            Text(
                text = themeOption.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}