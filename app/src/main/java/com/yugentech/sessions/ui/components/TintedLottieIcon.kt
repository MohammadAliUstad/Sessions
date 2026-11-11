package com.yugentech.sessions.ui.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

/**
 * A looping Lottie icon (a Lordicon "solid" one, say) drawn all in [color] instead of the colours
 * it was made in, so it fits the theme in light and dark like any other icon.
 */
@Composable
fun TintedLottieIcon(
    @RawRes animation: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
    val colorFilter = remember(color) { SimpleColorFilter(color.toArgb()) }
    val dynamicProperties = rememberLottieDynamicProperties(
        // "**" matches every layer: the whole icon takes the one colour
        rememberLottieDynamicProperty(LottieProperty.COLOR_FILTER, colorFilter, "**")
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        dynamicProperties = dynamicProperties,
        modifier = modifier
    )
}
