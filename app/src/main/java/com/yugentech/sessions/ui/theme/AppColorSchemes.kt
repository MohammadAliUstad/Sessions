package com.yugentech.sessions.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

object AppColorSchemes {

    val MonochromeLight = lightColorScheme(
        primary = Pure_Black,
        onPrimary = Pure_White,
        primaryContainer = Light_Gray,
        onPrimaryContainer = Pure_Black,

        secondary = Gray,
        onSecondary = Pure_White,
        secondaryContainer = Very_Light_Gray,
        onSecondaryContainer = Pure_Black,

        tertiary = Medium_Dark_Gray,
        onTertiary = Pure_White,
        tertiaryContainer = Light_Gray,
        onTertiaryContainer = Pure_Black,

        background = Pure_White,
        onBackground = Pure_Black,
        surface = Very_Light_Gray,
        onSurface = Pure_Black,
        surfaceVariant = Light_Gray,
        onSurfaceVariant = Dark_Gray,

        error = RedColors.Red700,
        onError = Pure_White,
        errorContainer = RedColors.Red100,
        onErrorContainer = RedColors.Red800,

        outline = Gray,
        outlineVariant = Light_Gray,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = Dark_Gray,
        inverseOnSurface = Pure_White,
        inversePrimary = Very_Light_Gray
    )
    val MonochromeDark = darkColorScheme(
        primary = Pure_White,
        onPrimary = Pure_Black,
        primaryContainer = Medium_Dark_Gray,
        onPrimaryContainer = Very_Light_Gray,

        secondary = Light_Gray,
        onSecondary = Pure_Black,
        secondaryContainer = Dark_Gray,
        onSecondaryContainer = Very_Light_Gray,

        tertiary = Gray,
        onTertiary = Pure_White,
        tertiaryContainer = Medium_Dark_Gray,
        onTertiaryContainer = Very_Light_Gray,

        background = Pure_Black,
        onBackground = Pure_White,
        surface = Dark_Gray,
        onSurface = Pure_White,
        surfaceVariant = Medium_Dark_Gray,
        onSurfaceVariant = Very_Light_Gray,

        error = RedColors.Red300,
        onError = RedColors.Red900,
        errorContainer = RedColors.Red800,
        onErrorContainer = RedColors.Red200,

        outline = Light_Gray,
        outlineVariant = Gray,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = Very_Light_Gray,
        inverseOnSurface = Pure_Black,
        inversePrimary = Dark_Gray
    )

    val BlueLight = lightColorScheme(
        primary = BlueColors.Blue700,
        onPrimary = Pure_White,
        primaryContainer = BlueColors.Blue100,
        onPrimaryContainer = BlueColors.Blue900,

        secondary = BlueColors.Blue600,
        onSecondary = Pure_White,
        secondaryContainer = BlueColors.Blue50,
        onSecondaryContainer = BlueColors.Blue800,

        tertiary = BlueColors.Blue500,
        onTertiary = Pure_White,
        tertiaryContainer = BlueColors.Blue200,
        onTertiaryContainer = BlueColors.Blue800,

        background = Pure_White,
        onBackground = BlueColors.BlueDark100,
        surface = BlueColors.Blue50,
        onSurface = BlueColors.BlueDark100,
        surfaceVariant = BlueColors.Blue100,
        onSurfaceVariant = BlueColors.BlueDark300,

        error = RedColors.Red700,
        onError = Pure_White,
        errorContainer = RedColors.Red100,
        onErrorContainer = RedColors.Red800,

        outline = BlueColors.Blue400,
        outlineVariant = BlueColors.Blue200,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = BlueColors.BlueDark200,
        inverseOnSurface = BlueColors.Blue100,
        inversePrimary = BlueColors.Blue300
    )
    val BlueDark = darkColorScheme(
        primary = BlueColors.Blue300,
        onPrimary = BlueColors.Blue900,
        primaryContainer = BlueColors.Blue800,
        onPrimaryContainer = BlueColors.Blue100,

        secondary = BlueColors.Blue200,
        onSecondary = BlueColors.Blue800,
        secondaryContainer = BlueColors.BlueDark300,
        onSecondaryContainer = BlueColors.Blue100,

        tertiary = BlueColors.Blue400,
        onTertiary = BlueColors.Blue900,
        tertiaryContainer = BlueColors.BlueDark400,
        onTertiaryContainer = BlueColors.Blue200,

        background = BlueColors.BlueDark100,
        onBackground = BlueColors.Blue100,
        surface = BlueColors.BlueDark200,
        onSurface = BlueColors.Blue100,
        surfaceVariant = BlueColors.BlueDark300,
        onSurfaceVariant = BlueColors.Blue200,

        error = RedColors.Red300,
        onError = RedColors.Red900,
        errorContainer = RedColors.Red800,
        onErrorContainer = RedColors.Red200,

        outline = BlueColors.Blue400,
        outlineVariant = BlueColors.BlueDark400,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = BlueColors.Blue100,
        inverseOnSurface = BlueColors.BlueDark100,
        inversePrimary = BlueColors.Blue700
    )

    val GreenLight = lightColorScheme(
        primary = GreenColors.Green700,
        onPrimary = Pure_White,
        primaryContainer = GreenColors.Green100,
        onPrimaryContainer = GreenColors.Green900,

        secondary = GreenColors.Green600,
        onSecondary = Pure_White,
        secondaryContainer = GreenColors.Green50,
        onSecondaryContainer = GreenColors.Green800,

        tertiary = GreenColors.Green500,
        onTertiary = Pure_White,
        tertiaryContainer = GreenColors.Green200,
        onTertiaryContainer = GreenColors.Green800,

        background = Pure_White,
        onBackground = GreenColors.GreenDark100,
        surface = GreenColors.Green50,
        onSurface = GreenColors.GreenDark100,
        surfaceVariant = GreenColors.Green100,
        onSurfaceVariant = GreenColors.GreenDark300,

        error = RedColors.Red700,
        onError = Pure_White,
        errorContainer = RedColors.Red100,
        onErrorContainer = RedColors.Red800,

        outline = GreenColors.Green400,
        outlineVariant = GreenColors.Green200,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = GreenColors.GreenDark200,
        inverseOnSurface = GreenColors.Green100,
        inversePrimary = GreenColors.Green300
    )
    val GreenDark = darkColorScheme(
        primary = GreenColors.Green300,
        onPrimary = GreenColors.Green900,
        primaryContainer = GreenColors.Green800,
        onPrimaryContainer = GreenColors.Green100,

        secondary = GreenColors.Green200,
        onSecondary = GreenColors.Green800,
        secondaryContainer = GreenColors.GreenDark300,
        onSecondaryContainer = GreenColors.Green100,

        tertiary = GreenColors.Green400,
        onTertiary = GreenColors.Green900,
        tertiaryContainer = GreenColors.GreenDark400,
        onTertiaryContainer = GreenColors.Green200,

        background = GreenColors.GreenDark100,
        onBackground = GreenColors.Green100,
        surface = GreenColors.GreenDark200,
        onSurface = GreenColors.Green100,
        surfaceVariant = GreenColors.GreenDark300,
        onSurfaceVariant = GreenColors.Green200,

        error = RedColors.Red300,
        onError = RedColors.Red900,
        errorContainer = RedColors.Red800,
        onErrorContainer = RedColors.Red200,

        outline = GreenColors.Green400,
        outlineVariant = GreenColors.GreenDark400,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = GreenColors.Green100,
        inverseOnSurface = GreenColors.GreenDark100,
        inversePrimary = GreenColors.Green700
    )

    val OrangeLight = lightColorScheme(
        primary = OrangeColors.Orange700,
        onPrimary = Pure_White,
        primaryContainer = OrangeColors.Orange100,
        onPrimaryContainer = OrangeColors.Orange900,

        secondary = OrangeColors.Orange600,
        onSecondary = Pure_White,
        secondaryContainer = OrangeColors.Orange50,
        onSecondaryContainer = OrangeColors.Orange800,

        tertiary = OrangeColors.Orange500,
        onTertiary = Pure_White,
        tertiaryContainer = OrangeColors.Orange200,
        onTertiaryContainer = OrangeColors.Orange800,

        background = Pure_White,
        onBackground = OrangeColors.OrangeDark100,
        surface = OrangeColors.Orange50,
        onSurface = OrangeColors.OrangeDark100,
        surfaceVariant = OrangeColors.Orange100,
        onSurfaceVariant = OrangeColors.OrangeDark300,

        error = RedColors.Red700,
        onError = Pure_White,
        errorContainer = RedColors.Red100,
        onErrorContainer = RedColors.Red800,

        outline = OrangeColors.Orange400,
        outlineVariant = OrangeColors.Orange200,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = OrangeColors.OrangeDark200,
        inverseOnSurface = OrangeColors.Orange100,
        inversePrimary = OrangeColors.Orange300
    )
    val OrangeDark = darkColorScheme(
        primary = OrangeColors.Orange300,
        onPrimary = OrangeColors.Orange900,
        primaryContainer = OrangeColors.Orange800,
        onPrimaryContainer = OrangeColors.Orange100,

        secondary = OrangeColors.Orange200,
        onSecondary = OrangeColors.Orange800,
        secondaryContainer = OrangeColors.OrangeDark300,
        onSecondaryContainer = OrangeColors.Orange100,

        tertiary = OrangeColors.Orange400,
        onTertiary = OrangeColors.Orange900,
        tertiaryContainer = OrangeColors.OrangeDark400,
        onTertiaryContainer = OrangeColors.Orange200,

        background = OrangeColors.OrangeDark100,
        onBackground = OrangeColors.Orange100,
        surface = OrangeColors.OrangeDark200,
        onSurface = OrangeColors.Orange100,
        surfaceVariant = OrangeColors.OrangeDark300,
        onSurfaceVariant = OrangeColors.Orange200,

        error = RedColors.Red300,
        onError = RedColors.Red900,
        errorContainer = RedColors.Red800,
        onErrorContainer = RedColors.Red200,

        outline = OrangeColors.Orange400,
        outlineVariant = OrangeColors.OrangeDark400,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = OrangeColors.Orange100,
        inverseOnSurface = OrangeColors.OrangeDark100,
        inversePrimary = OrangeColors.Orange700
    )

    val PurpleLight = lightColorScheme(
        primary = PurpleColors.Purple700,
        onPrimary = Pure_White,
        primaryContainer = PurpleColors.Purple100,
        onPrimaryContainer = PurpleColors.Purple900,

        secondary = PurpleColors.Purple600,
        onSecondary = Pure_White,
        secondaryContainer = PurpleColors.Purple50,
        onSecondaryContainer = PurpleColors.Purple800,

        tertiary = PurpleColors.Purple500,
        onTertiary = Pure_White,
        tertiaryContainer = PurpleColors.Purple200,
        onTertiaryContainer = PurpleColors.Purple800,

        background = Pure_White,
        onBackground = PurpleColors.PurpleDark100,
        surface = PurpleColors.Purple50,
        onSurface = PurpleColors.PurpleDark100,
        surfaceVariant = PurpleColors.Purple100,
        onSurfaceVariant = PurpleColors.PurpleDark300,

        error = RedColors.Red700,
        onError = Pure_White,
        errorContainer = RedColors.Red100,
        onErrorContainer = RedColors.Red800,

        outline = PurpleColors.Purple400,
        outlineVariant = PurpleColors.Purple200,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = PurpleColors.PurpleDark200,
        inverseOnSurface = PurpleColors.Purple100,
        inversePrimary = PurpleColors.Purple300
    )
    val PurpleDark = darkColorScheme(
        primary = PurpleColors.Purple300,
        onPrimary = PurpleColors.Purple900,
        primaryContainer = PurpleColors.Purple800,
        onPrimaryContainer = PurpleColors.Purple100,

        secondary = PurpleColors.Purple200,
        onSecondary = PurpleColors.Purple800,
        secondaryContainer = PurpleColors.PurpleDark300,
        onSecondaryContainer = PurpleColors.Purple100,

        tertiary = PurpleColors.Purple400,
        onTertiary = PurpleColors.Purple900,
        tertiaryContainer = PurpleColors.PurpleDark400,
        onTertiaryContainer = PurpleColors.Purple200,

        background = PurpleColors.PurpleDark100,
        onBackground = PurpleColors.Purple100,
        surface = PurpleColors.PurpleDark200,
        onSurface = PurpleColors.Purple100,
        surfaceVariant = PurpleColors.PurpleDark300,
        onSurfaceVariant = PurpleColors.Purple200,

        error = RedColors.Red300,
        onError = RedColors.Red900,
        errorContainer = RedColors.Red800,
        onErrorContainer = RedColors.Red200,

        outline = PurpleColors.Purple400,
        outlineVariant = PurpleColors.PurpleDark400,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = PurpleColors.Purple100,
        inverseOnSurface = PurpleColors.PurpleDark100,
        inversePrimary = PurpleColors.Purple700
    )

    val TealLight = lightColorScheme(
        primary = TealColors.Teal700,
        onPrimary = Pure_White,
        primaryContainer = TealColors.Teal100,
        onPrimaryContainer = TealColors.Teal900,

        secondary = TealColors.Teal600,
        onSecondary = Pure_White,
        secondaryContainer = TealColors.Teal50,
        onSecondaryContainer = TealColors.Teal800,

        tertiary = TealColors.Teal500,
        onTertiary = Pure_White,
        tertiaryContainer = TealColors.Teal200,
        onTertiaryContainer = TealColors.Teal800,

        background = Pure_White,
        onBackground = TealColors.TealDark100,
        surface = TealColors.Teal50,
        onSurface = TealColors.TealDark100,
        surfaceVariant = TealColors.Teal100,
        onSurfaceVariant = TealColors.TealDark300,

        error = RedColors.Red700,
        onError = Pure_White,
        errorContainer = RedColors.Red100,
        onErrorContainer = RedColors.Red800,

        outline = TealColors.Teal400,
        outlineVariant = TealColors.Teal200,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = TealColors.TealDark200,
        inverseOnSurface = TealColors.Teal100,
        inversePrimary = TealColors.Teal300
    )
    val TealDark = darkColorScheme(
        primary = TealColors.Teal300,
        onPrimary = TealColors.Teal900,
        primaryContainer = TealColors.Teal800,
        onPrimaryContainer = TealColors.Teal100,

        secondary = TealColors.Teal200,
        onSecondary = TealColors.Teal800,
        secondaryContainer = TealColors.TealDark300,
        onSecondaryContainer = TealColors.Teal100,

        tertiary = TealColors.Teal400,
        onTertiary = TealColors.Teal900,
        tertiaryContainer = TealColors.TealDark400,
        onTertiaryContainer = TealColors.Teal200,

        background = TealColors.TealDark100,
        onBackground = TealColors.Teal100,
        surface = TealColors.TealDark200,
        onSurface = TealColors.Teal100,
        surfaceVariant = TealColors.TealDark300,
        onSurfaceVariant = TealColors.Teal200,

        error = RedColors.Red300,
        onError = RedColors.Red900,
        errorContainer = RedColors.Red800,
        onErrorContainer = RedColors.Red200,

        outline = TealColors.Teal400,
        outlineVariant = TealColors.TealDark400,
        scrim = Pure_Black.copy(alpha = 0.32f),
        inverseSurface = TealColors.Teal100,
        inverseOnSurface = TealColors.TealDark100,
        inversePrimary = TealColors.Teal700
    )
}