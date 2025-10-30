package com.yugentech.sessions.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** ---------------------- SPACING ---------------------- */
data class SpacingTokens(
    val xs: Dp,
    val s: Dp,
    val m: Dp,
    val l: Dp,
    val xl: Dp,
    val xxl: Dp
)

/** ---------------------- CORNERS ---------------------- */
data class CornerTokens(
    val small: Dp,
    val medium: Dp,
    val large: Dp
)

/** ---------------------- STROKES ---------------------- */
data class StrokeTokens(
    val thin: Dp,
    val medium: Dp,
    val thick: Dp
)

/** ---------------------- COMPONENT SIZES ---------------------- */
data class ComponentTokens(
    val iconSmall: Dp,
    val iconMedium: Dp,
    val iconLarge: Dp,
    val buttonSmall: Dp,
    val buttonMedium: Dp,
    val buttonLarge: Dp,
    val buttonHeight: Dp,
    val cardMinWidth: Dp,
    val cardElevation: Dp,
    val imageSizeSmall: Dp,
    val imageSizeMedium: Dp,
    val imageSizeLarge: Dp
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
    val caption: Int
)

/** ---------------------- DESIGN TOKEN MODEL ---------------------- */
data class DesignTokens(
    val spacing: SpacingTokens,
    val corners: CornerTokens,
    val components: ComponentTokens,
    val elevation: ElevationTokens,
    val strokeWidths: StrokeTokens,
    val typography: TypographyTokens // Added this line
)

/** ---------------------- COMPACT ---------------------- */
val TokensCompact = DesignTokens(
    spacing = SpacingTokens(4.dp, 8.dp, 16.dp, 24.dp, 32.dp, 48.dp),
    corners = CornerTokens(8.dp, 16.dp, 24.dp),
    components = ComponentTokens(
        iconSmall = 16.dp, iconMedium = 24.dp, iconLarge = 32.dp,
        buttonSmall = 40.dp, buttonMedium = 48.dp, buttonLarge = 56.dp,
        buttonHeight = 48.dp,
        cardMinWidth = 160.dp, cardElevation = 2.dp,
        imageSizeSmall = 48.dp, imageSizeMedium = 96.dp, imageSizeLarge = 160.dp
    ),
    elevation = ElevationTokens(0.dp, 2.dp, 4.dp, 8.dp, 12.dp),
    strokeWidths = StrokeTokens(1.dp, 2.dp, 4.dp),
    typography = TypographyTokens(  // Added this block
        body = 16,
        label = 16,
        caption = 12
    )
)

/** ---------------------- MEDIUM ---------------------- */
val TokensMedium = DesignTokens(
    spacing = SpacingTokens(6.dp, 12.dp, 20.dp, 28.dp, 40.dp, 56.dp),
    corners = CornerTokens(10.dp, 18.dp, 26.dp),
    components = ComponentTokens(
        iconSmall = 20.dp, iconMedium = 28.dp, iconLarge = 36.dp,
        buttonSmall = 44.dp, buttonMedium = 52.dp, buttonLarge = 64.dp,
        buttonHeight = 52.dp,
        cardMinWidth = 200.dp, cardElevation = 3.dp,
        imageSizeSmall = 64.dp, imageSizeMedium = 128.dp, imageSizeLarge = 200.dp
    ),
    elevation = ElevationTokens(0.dp, 3.dp, 6.dp, 10.dp, 16.dp),
    strokeWidths = StrokeTokens(1.dp, 3.dp, 5.dp),
    typography = TypographyTokens(  // Added this block
        body = 18,
        label = 18,
        caption = 14
    )
)

/** ---------------------- EXPANDED ---------------------- */
val TokensExpanded = DesignTokens(
    spacing = SpacingTokens(8.dp, 16.dp, 24.dp, 32.dp, 48.dp, 64.dp),
    corners = CornerTokens(12.dp, 20.dp, 32.dp),
    components = ComponentTokens(
        iconSmall = 24.dp, iconMedium = 32.dp, iconLarge = 40.dp,
        buttonSmall = 48.dp, buttonMedium = 60.dp, buttonLarge = 72.dp,
        buttonHeight = 60.dp,
        cardMinWidth = 240.dp, cardElevation = 4.dp,
        imageSizeSmall = 80.dp, imageSizeMedium = 160.dp, imageSizeLarge = 240.dp
    ),
    elevation = ElevationTokens(0.dp, 4.dp, 8.dp, 12.dp, 20.dp),
    strokeWidths = StrokeTokens(1.dp, 4.dp, 6.dp),
    typography = TypographyTokens(  // Added this block
        body = 20,
        label = 20,
        caption = 16
    )
)