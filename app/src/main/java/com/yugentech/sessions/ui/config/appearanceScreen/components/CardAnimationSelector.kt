package com.yugentech.sessions.ui.config.appearanceScreen.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.yugentech.sessions.theme.config.CardAnimationStyle
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.HeroCardBackground
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.SessionPulse
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroBrush
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroPalette
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.heroSurface
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.rememberEffectTime
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import org.koin.androidx.compose.koinViewModel

// Live previews of each session card animation, painted in the chosen card style; tap one to use it
@Composable
fun CardAnimationSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val cardStyle = themeConfig.cardStyle
    val palette = heroPalette(cardStyle)
    val brush = heroBrush()
    // The previews play as if a focus block were well under way and running
    val time = rememberEffectTime(running = true)
    val preview = SessionPulse(progress = 0.6f, running = true)
    // Moves on the tap itself, not when the saved setting comes back, so the outline starts at once
    var chosen by remember(themeConfig.cardAnimationStyle) { mutableStateOf(themeConfig.cardAnimationStyle) }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.AutoAwesome,
            title = "Card Animation"
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CardAnimationStyle.entries.chunked(2).forEach { rowStyles ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowStyles.forEach { style ->
                        val isSelected = style == chosen
                        val shape = RoundedCornerShape(18.dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(88.dp)
                                .clip(shape)
                                // No ripple and no press scale: the outline is the only feedback
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (!isSelected) {
                                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        chosen = style
                                        viewModel.updateTheme(themeConfig.copy(cardAnimationStyle = style))
                                    }
                                }
                                .heroSurface(cardStyle, brush, shape, drawBorder = false)
                                .crossfadeSelectionOutline(
                                    isSelected = isSelected,
                                    shape = shape,
                                    unselectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                        ) {
                            HeroCardBackground(
                                style = style,
                                time = time,
                                pulse = preview,
                                ink = palette.ink,
                                shade = palette.shade,
                                modifier = Modifier.matchParentSize()
                            )
                            Text(
                                text = style.label,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = palette.content,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            )
                        }
                    }
                    if (rowStyles.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
