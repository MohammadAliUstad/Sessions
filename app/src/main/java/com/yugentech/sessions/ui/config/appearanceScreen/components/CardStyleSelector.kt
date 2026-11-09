package com.yugentech.sessions.ui.config.appearanceScreen.components

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.config.CardStyle
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroBrush
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroPalette
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroSurface
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import org.koin.androidx.compose.koinViewModel

// Filled, Outlined and Glass, each shown as a small card painted that way
@Composable
fun CardStyleSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val brush = heroBrush()
    // Moves on the tap itself, not when the saved setting comes back, so the outline starts at once
    var chosen by remember(themeConfig.cardStyle) { mutableStateOf(themeConfig.cardStyle) }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.Style,
            title = stringResource(R.string.card_style_title)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CardStyle.entries.forEach { style ->
                val isSelected = style == chosen
                val palette = heroPalette(style)
                val outer = RoundedCornerShape(20.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(outer)
                        // No ripple and no press scale: the outline is the only feedback
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                chosen = style
                                viewModel.updateTheme(themeConfig.copy(cardStyle = style))
                            }
                        }
                        .crossfadeSelectionOutline(
                            isSelected = isSelected,
                            shape = outer,
                            unselectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .heroSurface(style, brush, RoundedCornerShape(16.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .background(palette.content.copy(alpha = 0.55f))
                            )
                            Spacer(Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(46.dp)
                                    .height(9.dp)
                                    .clip(CircleShape)
                                    .background(palette.content.copy(alpha = 0.9f))
                            )
                        }

                        SelectionTick(
                            isSelected = isSelected,
                            size = 20.dp,
                            iconSize = 13.dp,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(style.label),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = animateSelectionColor(
                            isSelected,
                            selected = MaterialTheme.colorScheme.onSurface,
                            unselected = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
