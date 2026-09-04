package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BottomNavBar(
    items: List<BottomBarScreen>,
    currentScreen: BottomBarScreen,
    onSelected: (BottomBarScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = NavigationBarDefaults.Elevation
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
                colors = NavigationBarItemDefaults.colors(
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
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(shouldPlay) {
        if (shouldPlay) isAnimating = true
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(jsonResId))
    val currentColor = LocalContentColor.current.toArgb()

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
        modifier = modifier.size(24.dp),
        isPlaying = isAnimating,
        iterations = 1,
        dynamicProperties = dynamicProperties,
        maintainOriginalImageBounds = true
    )

    // Reset isAnimating only when the animation completes
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            // LottieAnimation with iterations = 1 will stop playing.
            // We use composition?.duration to wait for the animation to finish
            // before resetting the state, allowing it to complete even if shouldPlay becomes false.
            composition?.let {
                kotlinx.coroutines.delay(it.duration.toLong().milliseconds)
                isAnimating = false
            }
        }
    }
}