package com.yugentech.sessions.ui.dash.components.mainScreen

import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.yugentech.sessions.R
import com.yugentech.sessions.navigation.screen.BottomBarScreen

@Composable
fun BottomNavBar(
    items: List<BottomBarScreen>,
    currentScreen: BottomBarScreen,
    onSelected: (BottomBarScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    // Standard M3 NavigationBar uses its default container colors natively
    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { screen ->
            val isSelected = currentScreen == screen
            val animationRes = when (screen) {
                BottomBarScreen.Profile -> R.raw.account
                BottomBarScreen.Home -> R.raw.clock
                BottomBarScreen.Settings -> R.raw.settings
            }

            NavigationBarItem(
                icon = {
                    AnimatedNavIcon(
                        jsonResId = animationRes,
                        shouldPlay = isSelected
                    )
                },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = { onSelected(screen) },
                alwaysShowLabel = true,
                // Explicitly defining the standard Material 3 color roles
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Composable
private fun AnimatedNavIcon(
    jsonResId: Int,
    shouldPlay: Boolean,
    modifier: Modifier = Modifier
) {
    var playCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(shouldPlay) {
        if (shouldPlay) {
            playCount++
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(jsonResId))

    // 1. Grab the current Material 3 role color provided by NavigationBarItem
    val currentColor = LocalContentColor.current.toArgb()

    // 2. Override all fills ("**") and strokes ("**") in the Lottie file
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = currentColor,
            keyPath = arrayOf("**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = currentColor,
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        composition = composition,
        modifier = modifier.size(28.dp),
        isPlaying = shouldPlay && playCount > 0,
        iterations = 1,
        dynamicProperties = dynamicProperties
    )
}