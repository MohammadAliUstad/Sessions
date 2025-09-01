package com.yugentech.sessions.ui.dash.components.mainScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yugentech.sessions.R

@Composable
fun SettingsIcon(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var playCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            playCount++
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.settings))

    LottieAnimation(
        composition = composition,
        modifier = modifier
            .clickable(enabled = !isSelected) { onClick() }
            .size(24.dp),
        isPlaying = isSelected && playCount > 0,
        iterations = 1
    )
}