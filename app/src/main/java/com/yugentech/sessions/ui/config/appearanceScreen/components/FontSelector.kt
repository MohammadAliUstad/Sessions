package com.yugentech.sessions.ui.config.appearanceScreen.components

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.AppFont
import com.yugentech.sessions.theme.builder.getFontFamily
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FontSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel
) {
    val currentFont by viewModel.currentFont.collectAsState()
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.TextFields,
            title = stringResource(R.string.font_title)
        )

        // No ripple on the chips either
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.xs)
            ) {
                items(AppFont.entries) { font ->
                    val selected = currentFont == font
                    // Plain colours, switched instantly: no fade between states
                    val container = if (selected) scheme.secondaryContainer else Color.Transparent
                    val content = if (selected) scheme.onSecondaryContainer else scheme.onSurfaceVariant
                    val outline = if (selected) Color.Transparent else scheme.outlineVariant
                    FilterChip(
                        selected = selected,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = container,
                            selectedContainerColor = container,
                            labelColor = content,
                            selectedLabelColor = content
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = outline,
                            selectedBorderColor = outline,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        ),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            viewModel.setFont(font)
                        },
                        label = {
                            Text(
                                text = font.displayName,
                                fontFamily = getFontFamily(font),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }
    }
}
