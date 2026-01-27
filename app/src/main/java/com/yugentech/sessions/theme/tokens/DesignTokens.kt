package com.yugentech.sessions.theme.tokens

import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.dimensions.ComponentTokens
import com.yugentech.sessions.theme.tokens.dimensions.CornerTokens
import com.yugentech.sessions.theme.tokens.dimensions.ElevationTokens
import com.yugentech.sessions.theme.tokens.dimensions.IconSizeTokens
import com.yugentech.sessions.theme.tokens.dimensions.SpacingTokens
import com.yugentech.sessions.theme.tokens.dimensions.StrokeTokens

// Aggregates all specific token categories into a single design system object
data class DesignTokens(
    val spacing: SpacingTokens,
    val corners: CornerTokens,
    val icons: IconSizeTokens,
    val components: ComponentTokens,
    val elevation: ElevationTokens,
    val strokeWidths: StrokeTokens
)

// Concrete implementation of values for standard phone screen sizes
val TokensCompact = DesignTokens(
    spacing = SpacingTokens(
        none = 0.dp,
        xxs = 2.dp,
        xs = 4.dp,
        xsSmall = 6.dp,
        s = 8.dp,
        sm = 12.dp,
        m = 16.dp,
        l = 24.dp,
        xl = 32.dp,
        xxl = 48.dp,
        jumbo  = 56.dp,
        edge  = 32.dp,
    ),
    corners = CornerTokens(
        small = 8.dp,
        smallMedium = 12.dp,
        medium = 16.dp,
        large = 24.dp,
        extraLarge = 30.dp,
        pill = 50.dp
    ),
    icons = IconSizeTokens(
        extraSmall = 8.dp,
        small = 16.dp,
        smallMedium = 18.dp,
        mediumSmall = 20.dp,
        medium = 24.dp,
        mediumLarge = 28.dp,
        large = 32.dp,
        extraLarge = 40.dp,
        xxl = 48.dp,
        huge = 56.dp
    ),
    components = ComponentTokens(
        dotSize = 8.dp,
        buttonSmall = 40.dp,
        buttonMedium = 48.dp,
        buttonLarge = 56.dp,
        fabSizeStandard = 72.dp,
        fabSizeLarge = 80.dp,
        buttonHeight = 48.dp,
        cardMinWidth = 160.dp,
        cardElevation = 2.dp,
        imageSizeSmall = 72.dp,
        imageSizeMedium = 88.dp,
        imageSizeLarge = 160.dp,
        imageSizeHuge = 220.dp,
        timerSize = 280.dp,
        bottomNavHeight = 80.dp,
        bottomNavPadding = 80.dp,
        controlBarItemSize = 96.dp,
        controlBarItemWidthWide = 128.dp,
        onboardingImageContainer = 280.dp,
        onboardingIndicatorHeight  = 10.dp,
    ),
    elevation = ElevationTokens(
        level0 = 0.dp,
        level1 = 2.dp,
        level2 = 4.dp,
        level3 = 8.dp,
        level4 = 12.dp
    ),
    strokeWidths = StrokeTokens(
        thin = 1.dp,
        medium = 2.dp,
        thick = 4.dp,
        extraThick = 6.dp
    )
)