package com.yugentech.sessions.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SpacingTokens(
    val xs: Dp,
    val s: Dp,
    val m: Dp,
    val l: Dp,
    val xl: Dp,
    val xxl: Dp
)

data class CornerTokens(
    val small: Dp,
    val medium: Dp,
    val large: Dp
)

data class TypeTokens(
    val display: Int,
    val title: Int,
    val subtitle: Int,
    val body: Int,
    val label: Int,
    val small: Int,
    val caption: Int
)

data class StrokeTokens(
    val thin: Dp,
    val medium: Dp,
    val thick: Dp
)

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

data class ElevationTokens(
    val level0: Dp,
    val level1: Dp,
    val level2: Dp,
    val level3: Dp,
    val level4: Dp
)

data class DesignTokens(
    val spacing: SpacingTokens,
    val corners: CornerTokens,
    val typography: TypeTokens,
    val components: ComponentTokens,
    val elevation: ElevationTokens,
    val strokeWidths: StrokeTokens // <-- added strokeWidths
)

val TokensCompact = DesignTokens(
    spacing = SpacingTokens(
        xs = 4.dp,
        s = 8.dp,
        m = 16.dp,
        l = 24.dp,
        xl = 32.dp,
        xxl = 48.dp
    ),
    corners = CornerTokens(
        small = 8.dp,
        medium = 16.dp,
        large = 24.dp
    ),
    typography = TypeTokens(
        display = 32,
        title = 22,
        subtitle = 18,
        body = 14,
        label = 12,
        small = 10,
        caption = 12
    ),
    components = ComponentTokens(
        iconSmall = 16.dp,
        iconMedium = 24.dp,
        iconLarge = 32.dp,
        buttonSmall = 40.dp,
        buttonMedium = 48.dp,
        buttonLarge = 56.dp,
        buttonHeight = 48.dp,
        cardMinWidth = 160.dp,
        cardElevation = 2.dp,
        imageSizeSmall = 48.dp,
        imageSizeMedium = 96.dp,
        imageSizeLarge = 160.dp
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
        thick = 4.dp
    )
)

val TokensMedium = TokensCompact.copy(
    spacing = SpacingTokens(
        xs = 6.dp,
        s = 12.dp,
        m = 20.dp,
        l = 28.dp,
        xl = 40.dp,
        xxl = 56.dp
    ),
    corners = CornerTokens(
        small = 10.dp,
        medium = 18.dp,
        large = 26.dp
    ),
    typography = TypeTokens(
        display = 36,
        title = 24,
        subtitle = 20,
        body = 16,
        label = 14,
        small = 12,
        caption = 14
    ),
    components = ComponentTokens(
        iconSmall = 20.dp,
        iconMedium = 28.dp,
        iconLarge = 36.dp,
        buttonSmall = 44.dp,
        buttonMedium = 52.dp,
        buttonLarge = 64.dp,
        buttonHeight = 52.dp,
        cardMinWidth = 200.dp,
        cardElevation = 3.dp,
        imageSizeSmall = 64.dp,
        imageSizeMedium = 128.dp,
        imageSizeLarge = 200.dp
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
        thick = 5.dp
    )
)

val TokensExpanded = TokensCompact.copy(
    spacing = SpacingTokens(
        xs = 8.dp,
        s = 16.dp,
        m = 24.dp,
        l = 32.dp,
        xl = 48.dp,
        xxl = 64.dp
    ),
    corners = CornerTokens(
        small = 12.dp,
        medium = 20.dp,
        large = 32.dp
    ),
    typography = TypeTokens(
        display = 40,
        title = 28,
        subtitle = 22,
        body = 18,
        label = 16,
        small = 14,
        caption = 16
    ),
    components = ComponentTokens(
        iconSmall = 24.dp,
        iconMedium = 32.dp,
        iconLarge = 40.dp,
        buttonSmall = 48.dp,
        buttonMedium = 60.dp,
        buttonLarge = 72.dp,
        buttonHeight = 60.dp,
        cardMinWidth = 240.dp,
        cardElevation = 4.dp,
        imageSizeSmall = 80.dp,
        imageSizeMedium = 160.dp,
        imageSizeLarge = 240.dp
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
        thick = 6.dp
    )
)