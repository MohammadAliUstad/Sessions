package com.yugentech.sessions.theme.tokens

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
    val extraThick: Dp
)

/** ---------------------- ICON SIZES ---------------------- */
data class IconSizeTokens(
    val extraSmall: Dp,      // For dots, indicators
    val small: Dp,           // For secondary icons, list items
    val smallMedium: Dp,     // In-between size
    val mediumSmall: Dp,     // Slightly larger than small
    val medium: Dp,          // Default icon size (most common)
    val mediumLarge: Dp,     // Emphasized icons
    val large: Dp,           // For prominent actions
    val extraLarge: Dp,      // For hero icons, empty states
    val huge: Dp             // For special cases, splash screens
)

/** ---------------------- COMPONENT SIZES ---------------------- */
data class ComponentTokens(
    val dotSize: Dp,
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
    val timerSize: Dp
)

/** ---------------------- ELEVATIONS ---------------------- */
data class ElevationTokens(
    val level0: Dp,
    val level1: Dp,
    val level2: Dp,
    val level3: Dp,
    val level4: Dp
)

/** ---------------------- TYPOGRAPHY SIZES ---------------------- */
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
    val icons: IconSizeTokens,        // NEW: Icons integrated here
    val components: ComponentTokens,
    val elevation: ElevationTokens,
    val strokeWidths: StrokeTokens,
    val typography: TypographyTokens
)

/** ---------------------- COMPACT (Phones Portrait) ---------------------- */
val TokensCompact = DesignTokens(
    spacing = SpacingTokens(4.dp, 6.dp, 8.dp, 12.dp, 16.dp, 24.dp, 32.dp, 48.dp),
    corners = CornerTokens(8.dp, 12.dp, 16.dp, 24.dp, 30.dp),
    icons = IconSizeTokens(
        extraSmall = 8.dp,
        small = 16.dp,
        smallMedium = 18.dp,
        mediumSmall = 20.dp,
        medium = 24.dp,
        mediumLarge = 28.dp,
        large = 32.dp,
        extraLarge = 40.dp,
        huge = 56.dp
    ),
    components = ComponentTokens(
        dotSize = 8.dp,
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
        timerSize = 280.dp
    ),
    elevation = ElevationTokens(0.dp, 2.dp, 4.dp, 8.dp, 12.dp),
    strokeWidths = StrokeTokens(1.dp, 2.dp, 4.dp, 6.dp),
    typography = TypographyTokens(
        body = 16,
        label = 16,
        caption = 12,
        display = 56,
        headline = 28
    )
)

/** ---------------------- MEDIUM (Tablets Portrait, Phones Landscape) ---------------------- */
val TokensMedium = DesignTokens(
    spacing = SpacingTokens(6.dp, 9.dp, 12.dp, 16.dp, 20.dp, 28.dp, 40.dp, 56.dp),
    corners = CornerTokens(10.dp, 14.dp, 18.dp, 26.dp, 34.dp),
    icons = IconSizeTokens(
        extraSmall = 10.dp,
        small = 20.dp,
        smallMedium = 22.dp,
        mediumSmall = 24.dp,
        medium = 28.dp,
        mediumLarge = 32.dp,
        large = 36.dp,
        extraLarge = 48.dp,
        huge = 64.dp
    ),
    components = ComponentTokens(
        dotSize = 10.dp,
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
        timerSize = 320.dp
    ),
    elevation = ElevationTokens(0.dp, 3.dp, 6.dp, 10.dp, 16.dp),
    strokeWidths = StrokeTokens(1.dp, 3.dp, 5.dp, 8.dp),
    typography = TypographyTokens(
        body = 18,
        label = 18,
        caption = 14,
        display = 64,
        headline = 32
    )
)

/** ---------------------- EXPANDED (Tablets Landscape, Desktop) ---------------------- */
val TokensExpanded = DesignTokens(
    spacing = SpacingTokens(8.dp, 12.dp, 16.dp, 20.dp, 24.dp, 32.dp, 48.dp, 64.dp),
    corners = CornerTokens(12.dp, 16.dp, 20.dp, 32.dp, 40.dp),
    icons = IconSizeTokens(
        extraSmall = 12.dp,
        small = 24.dp,
        smallMedium = 26.dp,
        mediumSmall = 28.dp,
        medium = 32.dp,
        mediumLarge = 36.dp,
        large = 40.dp,
        extraLarge = 56.dp,
        huge = 72.dp
    ),
    components = ComponentTokens(
        dotSize = 12.dp,
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