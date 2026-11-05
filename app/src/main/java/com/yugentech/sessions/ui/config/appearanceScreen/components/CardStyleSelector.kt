package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.foundation.background
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
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
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

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.Style,
            title = "Card Style"
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CardStyle.entries.forEach { style ->
                val isSelected = style == themeConfig.cardStyle
                val palette = heroPalette(style)
                val outer = RoundedCornerShape(20.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .pressClickable(shape = outer, pressedScale = 0.95f) {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                viewModel.updateTheme(themeConfig.copy(cardStyle = style))
                            }
                        }
                        .selectionOutline(
                            isSelected = isSelected,
                            shape = outer,
                            unselectedWidth = 0.dp
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
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = style.label,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
