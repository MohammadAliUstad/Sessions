package com.yugentech.sessions.theme.tokens

import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.dimensions.ComponentTokens
import com.yugentech.sessions.theme.tokens.dimensions.CornerTokens
import com.yugentech.sessions.theme.tokens.dimensions.ElevationTokens
import com.yugentech.sessions.theme.tokens.dimensions.IconSizeTokens
import com.yugentech.sessions.theme.tokens.dimensions.SpacingTokens
import com.yugentech.sessions.theme.tokens.dimensions.StrokeTokens
import com.yugentech.sessions.theme.tokens.dimensions.animations.AnimationTokens

data class DesignTokens(
    val spacing: SpacingTokens,
    val corners: CornerTokens,
    val icons: IconSizeTokens,
    val components: ComponentTokens,
    val elevation: ElevationTokens,
    val strokeWidths: StrokeTokens,
    val animations: AnimationTokens = AnimationTokens()
)

val TokensCompact = DesignTokens(
    spacing = SpacingTokens(
        xs = 4.dp,
        xsSmall = 6.dp,
        s = 8.dp,
        sm = 12.dp,
        m = 16.dp,
        l = 24.dp,
        xl = 32.dp,
        xxl = 48.dp
    ),
    corners = CornerTokens(
        small = 8.dp,
        smallMedium = 12.dp,
        medium = 16.dp,
        large = 24.dp,
        extraLarge = 30.dp
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
        huge = 56.dp
    ),
    components = ComponentTokens(
        dotSize = 8.dp,
        buttonSmall = 40.dp,
        buttonMedium = 48.dp,
        buttonLarge = 56.dp,
        fabSize = 72.dp,
        buttonHeight = 48.dp,
        cardMinWidth = 160.dp,
        cardElevation = 2.dp,
        imageSizeSmall = 48.dp,
        imageSizeMedium = 96.dp,
        imageSizeLarge = 160.dp,
        timerSize = 280.dp
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

val TokensMedium = DesignTokens(
    spacing = SpacingTokens(
        xs = 6.dp,
        xsSmall = 9.dp,
        s = 12.dp,
        sm = 16.dp,
        m = 20.dp,
        l = 28.dp,
        xl = 40.dp,
        xxl = 56.dp
    ),
    corners = CornerTokens(
        small = 10.dp,
        smallMedium = 14.dp,
        medium = 18.dp,
        large = 26.dp,
        extraLarge = 34.dp
    ),
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
        fabSize = 80.dp,
        buttonHeight = 52.dp,
        cardMinWidth = 200.dp,
        cardElevation = 3.dp,
        imageSizeSmall = 64.dp,
        imageSizeMedium = 128.dp,
        imageSizeLarge = 200.dp,
        timerSize = 320.dp
    ),
    elevation = ElevationTokens(
        level0 = 0.dp,
        level1 = 3.dp,
        level2 = 6.dp,
        level3 = 10.dp,
        level4 = 16.dp
    ),
    strokeWidths = StrokeTokens(
        thin = 1.dp,
        medium = 3.dp,
        thick = 5.dp,
        extraThick = 8.dp
    )
)

val TokensExpanded = DesignTokens(
    spacing = SpacingTokens(
        xs = 8.dp,
        xsSmall = 12.dp,
        s = 16.dp,
        sm = 20.dp,
        m = 24.dp,
        l = 32.dp,
        xl = 48.dp,
        xxl = 64.dp
    ),
    corners = CornerTokens(
        small = 12.dp,
        smallMedium = 16.dp,
        medium = 20.dp,
        large = 32.dp,
        extraLarge = 40.dp
    ),
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
        fabSize = 88.dp,
        buttonHeight = 60.dp,
        cardMinWidth = 240.dp,
        cardElevation = 4.dp,
        imageSizeSmall = 80.dp,
        imageSizeMedium = 160.dp,
        imageSizeLarge = 240.dp,
        timerSize = 360.dp
    ),
    elevation = ElevationTokens(
        level0 = 0.dp,
        level1 = 4.dp,
        level2 = 8.dp,
        level3 = 12.dp,
        level4 = 20.dp
    ),
    strokeWidths = StrokeTokens(
        thin = 1.dp,
        medium = 4.dp,
        thick = 6.dp,
        extraThick = 10.dp
    )
)