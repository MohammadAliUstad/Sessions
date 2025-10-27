package com.yugentech.sessions.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Responsive dimensions that scale based on screen size
 * Small: < 360dp width (small phones)
 * Medium: 360-600dp width (normal phones)
 * Large: 600-840dp width (large phones, small tablets)
 * XLarge: > 840dp width (tablets)
 */

enum class ScreenSize {
    SMALL, MEDIUM, LARGE, XLARGE
}

@Composable
fun getScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    return when {
        screenWidth < 360 -> ScreenSize.SMALL
        screenWidth < 600 -> ScreenSize.MEDIUM
        screenWidth < 840 -> ScreenSize.LARGE
        else -> ScreenSize.XLARGE
    }
}

@Composable
fun getScreenHeight(): Int {
    return LocalConfiguration.current.screenHeightDp
}

@Composable
fun getScreenWidth(): Int {
    return LocalConfiguration.current.screenWidthDp
}

/**
 * Responsive dimensions object
 * Provides scaled dimensions based on screen size
 */
object ResponsiveDimensions {
    
    @Composable
    fun horizontalPadding(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 16.dp
            ScreenSize.MEDIUM -> 24.dp
            ScreenSize.LARGE -> 32.dp
            ScreenSize.XLARGE -> 40.dp
        }
    }
    
    @Composable
    fun verticalPadding(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 16.dp
            ScreenSize.MEDIUM -> 24.dp
            ScreenSize.LARGE -> 32.dp
            ScreenSize.XLARGE -> 40.dp
        }
    }
    
    @Composable
    fun titleTextSize(): TextUnit {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 20.sp
            ScreenSize.MEDIUM -> 24.sp
            ScreenSize.LARGE -> 28.sp
            ScreenSize.XLARGE -> 32.sp
        }
    }
    
    @Composable
    fun timerTextSize(): TextUnit {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 48.sp
            ScreenSize.MEDIUM -> 64.sp
            ScreenSize.LARGE -> 72.sp
            ScreenSize.XLARGE -> 80.sp
        }
    }
    
    @Composable
    fun timerCircleSize(): Dp {
        val screenHeight = getScreenHeight()
        return when (getScreenSize()) {
            ScreenSize.SMALL -> (screenHeight * 0.28f).dp.coerceIn(180.dp, 220.dp)
            ScreenSize.MEDIUM -> (screenHeight * 0.32f).dp.coerceIn(220.dp, 280.dp)
            ScreenSize.LARGE -> (screenHeight * 0.35f).dp.coerceIn(280.dp, 320.dp)
            ScreenSize.XLARGE -> (screenHeight * 0.35f).dp.coerceIn(300.dp, 400.dp)
        }
    }
    
    @Composable
    fun playButtonSize(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 56.dp
            ScreenSize.MEDIUM -> 64.dp
            ScreenSize.LARGE -> 72.dp
            ScreenSize.XLARGE -> 80.dp
        }
    }
    
    @Composable
    fun playButtonIconSize(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 24.dp
            ScreenSize.MEDIUM -> 28.dp
            ScreenSize.LARGE -> 32.dp
            ScreenSize.XLARGE -> 36.dp
        }
    }
    
    @Composable
    fun cardElevation(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 0.dp
            ScreenSize.MEDIUM -> 0.dp
            ScreenSize.LARGE -> 2.dp
            ScreenSize.XLARGE -> 4.dp
        }
    }
    
    @Composable
    fun cornerRadius(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 20.dp
            ScreenSize.MEDIUM -> 24.dp
            ScreenSize.LARGE -> 28.dp
            ScreenSize.XLARGE -> 32.dp
        }
    }
    
    @Composable
    fun smallCornerRadius(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 12.dp
            ScreenSize.MEDIUM -> 16.dp
            ScreenSize.LARGE -> 18.dp
            ScreenSize.XLARGE -> 20.dp
        }
    }
    
    @Composable
    fun spacing(): Spacing {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> SmallSpacing
            ScreenSize.MEDIUM -> MediumSpacing
            ScreenSize.LARGE -> LargeSpacing
            ScreenSize.XLARGE -> XLargeSpacing
        }
    }
}

/**
 * Spacing scale for different screen sizes
 */
interface Spacing {
    val extraSmall: Dp
    val small: Dp
    val medium: Dp
    val large: Dp
    val extraLarge: Dp
}

object SmallSpacing : Spacing {
    override val extraSmall = 4.dp
    override val small = 8.dp
    override val medium = 12.dp
    override val large = 16.dp
    override val extraLarge = 20.dp
}

object MediumSpacing : Spacing {
    override val extraSmall = 4.dp
    override val small = 8.dp
    override val medium = 16.dp
    override val large = 24.dp
    override val extraLarge = 32.dp
}

object LargeSpacing : Spacing {
    override val extraSmall = 6.dp
    override val small = 12.dp
    override val medium = 20.dp
    override val large = 28.dp
    override val extraLarge = 40.dp
}

object XLargeSpacing : Spacing {
    override val extraSmall = 8.dp
    override val small = 16.dp
    override val medium = 24.dp
    override val large = 32.dp
    override val extraLarge = 48.dp
}