package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.foundation.layout.WindowInsets // Make sure this is imported
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        NavigationBar(
            modifier = Modifier.height(92.dp).padding(bottom = 16.dp),
            windowInsets = WindowInsets(0.dp),
            containerColor = Color.Transparent
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
        isPlaying = shouldPlay && playCount > 0,
        iterations = 1,
        dynamicProperties = dynamicProperties
    )
}