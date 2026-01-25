package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.getters.AppFont
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.common.SectionHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FontSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel
) {
    val currentFont by viewModel.currentFont.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.TextFields,
            title = "App Font"
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.none),
            modifier = Modifier.fillMaxWidth()
        ) {
            AppFont.entries.forEach { font ->
                FontOptionToggle(
                    font = font,
                    isSelected = currentFont == font,
                    onSelect = { viewModel.setFont(font) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FontOptionToggle(
    font: AppFont,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    ToggleButton(
        checked = isSelected,
        onCheckedChange = { onSelect() },
        modifier = Modifier,
        shapes = ToggleButtonShapes(
            shape = RoundedCornerShape(MaterialTheme.corners.pill),
            pressedShape = RoundedCornerShape(MaterialTheme.corners.pill),
            checkedShape = RoundedCornerShape(MaterialTheme.corners.small)
        ),
        colors = ToggleButtonDefaults.toggleButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            checkedContainerColor = MaterialTheme.colorScheme.primary,
            checkedContentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = font.displayName,
            fontFamily = font.toFontFamily(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.s,
                vertical = MaterialTheme.spacing.xs
            )
        )
    }
}