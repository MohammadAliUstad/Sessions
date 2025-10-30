package com.yugentech.sessions.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** ---------------------- SPACING ---------------------- */
data class SpacingTokens(
    val xs: Dp,
    val xsSmall: Dp,
    val s: Dp,
    val sm: Dp,
    val m: Dp,
    val l: Dp,
    val xl: Dp,
    val xxl: Dp
)

/** ---------------------- CORNERS ---------------------- */
data class CornerTokens(
    val small: Dp,
    val smallMedium: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
)

/** ---------------------- STROKES ---------------------- */
data class StrokeTokens(
    val thin: Dp,
    val medium: Dp,
    val thick: Dp,
    val extraThick: Dp // Added this
)

/** ---------------------- COMPONENT SIZES ---------------------- */
data class ComponentTokens(
    val iconSmall: Dp,
    val dotSize: Dp,
    val iconSmallMedium: Dp,
    val iconMediumSmall: Dp,
    val iconMedium: Dp,
    val iconMediumLarge: Dp,
    val iconLarge: Dp,
    val buttonSmall: Dp,
    val buttonMedium: Dp,
    val buttonLarge: Dp,
    val fabSize: Dp,
    val buttonHeight: Dp,
    val cardMinWidth: Dp,
    val cardElevation: Dp,
    val imageSizeSmall: Dp,
    val imageSizeMedium: Dp,
    val imageSizeLarge: Dp,
    val timerSize: Dp // Added this
)

/** ---------------------- ELEVATIONS ---------------------- */
data class ElevationTokens(
    val level0: Dp,
    val level1: Dp,
    val level2: Dp,
    val level3: Dp,
    val level4: Dp
)

/** ---------------------- TYPOGRAPHY ---------------------- */
data class TypographyTokens(
    val body: Int,
    val label: Int,
    val caption: Int,
    val display: Int,
    val headline: Int
)

/** ---------------------- DESIGN TOKEN MODEL ---------------------- */
data class DesignTokens(
    val spacing: SpacingTokens,
    val corners: CornerTokens,
    val components: ComponentTokens,
    val elevation: ElevationTokens,
    val strokeWidths: StrokeTokens,
    val typography: TypographyTokens
)

/** ---------------------- COMPACT ---------------------- */
val TokensCompact = DesignTokens(
    spacing = SpacingTokens(4.dp, 6.dp, 8.dp, 12.dp, 16.dp, 24.dp, 32.dp, 48.dp),
    corners = CornerTokens(8.dp, 12.dp, 16.dp, 24.dp, 30.dp),
    components = ComponentTokens(
        iconSmall = 16.dp,
        iconSmallMedium = 18.dp,
        iconMediumSmall = 20.dp,
        iconMedium = 24.dp,
        iconMediumLarge = 28.dp,
        iconLarge = 32.dp,
        buttonSmall = 40.dp,
        buttonMedium = 48.dp,
        buttonLarge = 56.dp,
        fabSize = 64.dp,
        buttonHeight = 48.dp,
        cardMinWidth = 160.dp,
        cardElevation = 2.dp,
        imageSizeSmall = 48.dp,
        imageSizeMedium = 96.dp,
        imageSizeLarge = 160.dp,
        timerSize = 280.dp,
        dotSize = 8.dp
    ),
    elevation = ElevationTokens(0.dp, 2.dp, 4.dp, 8.dp, 12.dp),
    strokeWidths = StrokeTokens(1.dp, 2.dp, 4.dp, 6.dp), // Added value
    typography = TypographyTokens(
        body = 16,
        label = 16,
        caption = 12,
        display = 56,
        headline = 28
    )
)

/** ---------------------- MEDIUM ---------------------- */
val TokensMedium = DesignTokens(
    spacing = SpacingTokens(6.dp, 9.dp, 12.dp, 16.dp, 20.dp, 28.dp, 40.dp, 56.dp),
    corners = CornerTokens(10.dp, 14.dp, 18.dp, 26.dp, 34.dp),
    components = ComponentTokens(
        iconSmall = 20.dp,
        iconSmallMedium = 22.dp,
        iconMediumSmall = 24.dp,
        iconMedium = 28.dp,
        iconMediumLarge = 32.dp,
        iconLarge = 36.dp,
        buttonSmall = 44.dp,
        buttonMedium = 52.dp,
        buttonLarge = 64.dp,
        fabSize = 72.dp,
        buttonHeight = 52.dp,
        cardMinWidth = 200.dp,
        cardElevation = 3.dp,
        imageSizeSmall = 64.dp,
        imageSizeMedium = 128.dp,
        imageSizeLarge = 200.dp,
        timerSize = 320.dp,
        dotSize = 10.dp
    ),
    elevation = ElevationTokens(0.dp, 3.dp, 6.dp, 10.dp, 16.dp),
    strokeWidths = StrokeTokens(1.dp, 3.dp, 5.dp, 8.dp), // Added value
    typography = TypographyTokens(
        body = 18,
        label = 18,
        caption = 14,
        display = 64,
        headline = 32
    )
)

/** ---------------------- EXPANDED ---------------------- */
val TokensExpanded = DesignTokens(
    spacing = SpacingTokens(8.dp, 12.dp, 16.dp, 20.dp, 24.dp, 32.dp, 48.dp, 64.dp),
    corners = CornerTokens(12.dp, 16.dp, 20.dp, 32.dp, 40.dp),
    components = ComponentTokens(
        iconSmall = 24.dp,
        iconSmallMedium = 26.dp,
        iconMediumSmall = 28.dp,
        iconMedium = 32.dp,
        dotSize = 12.dp,
        iconMediumLarge = 36.dp,
        iconLarge = 40.dp,
        buttonSmall = 48.dp,
        buttonMedium = 60.dp,
        buttonLarge = 72.dp,
        fabSize = 80.dp,
        buttonHeight = 60.dp,
        cardMinWidth = 240.dp,
        cardElevation = 4.dp,
        imageSizeSmall = 80.dp,
        imageSizeMedium = 160.dp,
        imageSizeLarge = 240.dp,
        timerSize = 360.dp
    ),
    elevation = ElevationTokens(0.dp, 4.dp, 8.dp, 12.dp, 20.dp),
    strokeWidths = StrokeTokens(1.dp, 4.dp, 6.dp, 10.dp),
    typography = TypographyTokens(
        body = 20,
        label = 20,
        caption = 16,
        display = 72,
        headline = 36
    )
)