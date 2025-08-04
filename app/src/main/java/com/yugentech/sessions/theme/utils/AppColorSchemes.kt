package com.yugentech.sessions.theme.utils

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object AppColorSchemes {

    val MonochromeLight = lightColorScheme(
        primary = Color(0xFF1C1B1F),           // Rich black
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE6E1E5),   // Warm light gray
        onPrimaryContainer = Color(0xFF1C1B1F),

        secondary = Color(0xFF605D62),          // Medium gray
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE6E1E5),
        onSecondaryContainer = Color(0xFF1C1B1F),

        tertiary = Color(0xFF7C5260),           // Warm accent
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD8E4),
        onTertiaryContainer = Color(0xFF31101D),

        background = Color(0xFFFFFBFE),
        onBackground = Color(0xFF1C1B1F),
        surface = Color(0xFFFFFBFE),
        onSurface = Color(0xFF1C1B1F),
        surfaceVariant = Color(0xFFE7E0E4),
        onSurfaceVariant = Color(0xFF49454F),
        surfaceContainer = Color(0xFFF3EDF7),
        surfaceContainerLow = Color(0xFFFEF7FF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerHigh = Color(0xFFECE6F0),
        surfaceContainerHighest = Color(0xFFE6E0E9),

        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        outline = Color(0xFF79747E),
        outlineVariant = Color(0xFFCAC4D0),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF313033),
        inverseOnSurface = Color(0xFFF4EFF4),
        inversePrimary = Color(0xFFCAC5CD),

        surfaceBright = Color(0xFFFFFBFE),
        surfaceDim = Color(0xFFDDD8DD),
        surfaceTint = Color(0xFF1C1B1F)
    )
    val MonochromeDark = darkColorScheme(
        primary = Color(0xFFCAC5CD),
        onPrimary = Color(0xFF322F35),
        primaryContainer = Color(0xFF49454F),
        onPrimaryContainer = Color(0xFFE6E1E5),

        secondary = Color(0xFFCBC2C7),
        onSecondary = Color(0xFF332D32),
        secondaryContainer = Color(0xFF4A4458),
        onSecondaryContainer = Color(0xFFE7DEE3),

        tertiary = Color(0xFFEFB8C8),
        onTertiary = Color(0xFF492532),
        tertiaryContainer = Color(0xFF633B48),
        onTertiaryContainer = Color(0xFFFFD8E4),

        background = Color(0xFF141218),
        onBackground = Color(0xFFE6E1E5),
        surface = Color(0xFF141218),
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF49454F),
        onSurfaceVariant = Color(0xFFCAC4D0),
        surfaceContainer = Color(0xFF211F26),
        surfaceContainerLow = Color(0xFF1C1B1F),
        surfaceContainerLowest = Color(0xFF0F0D13),
        surfaceContainerHigh = Color(0xFF2B2930),
        surfaceContainerHighest = Color(0xFF36343B),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        outline = Color(0xFF938F99),
        outlineVariant = Color(0xFF49454F),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE6E1E5),
        inverseOnSurface = Color(0xFF313033),
        inversePrimary = Color(0xFF625B71),

        surfaceBright = Color(0xFF3B383E),
        surfaceDim = Color(0xFF141218),
        surfaceTint = Color(0xFFCAC5CD)
    )

    val BlueLight = lightColorScheme(
        primary = Color(0xFF0061A4),           // Deep ocean blue
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFD1E4FF),   // Light sky blue
        onPrimaryContainer = Color(0xFF001D36),

        secondary = Color(0xFF535F70),          // Slate blue
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD7E3F7),
        onSecondaryContainer = Color(0xFF101C2B),

        tertiary = Color(0xFF6B5778),           // Purple accent
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFF2DAFF),
        onTertiaryContainer = Color(0xFF251431),

        background = Color(0xFFFDFCFF),
        onBackground = Color(0xFF1A1C1E),
        surface = Color(0xFFFDFCFF),
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFDFE2EB),
        onSurfaceVariant = Color(0xFF43474E),
        surfaceContainer = Color(0xFFF1F4FD),
        surfaceContainerLow = Color(0xFFFBFEFF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerHigh = Color(0xFFEBEEF7),
        surfaceContainerHighest = Color(0xFFE5E8F1),

        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        outline = Color(0xFF73777F),
        outlineVariant = Color(0xFFC3C7CF),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2F3033),
        inverseOnSurface = Color(0xFFF1F0F4),
        inversePrimary = Color(0xFF9ECAFF),

        surfaceBright = Color(0xFFFDFCFF),
        surfaceDim = Color(0xFFDDDAE0),
        surfaceTint = Color(0xFF0061A4)
    )
    val BlueDark = darkColorScheme(
        primary = Color(0xFF9ECAFF),
        onPrimary = Color(0xFF003258),
        primaryContainer = Color(0xFF00497D),
        onPrimaryContainer = Color(0xFFD1E4FF),

        secondary = Color(0xFFBBC7DB),
        onSecondary = Color(0xFF253140),
        secondaryContainer = Color(0xFF3B4858),
        onSecondaryContainer = Color(0xFFD7E3F7),

        tertiary = Color(0xFFD6BEE4),
        onTertiary = Color(0xFF3B2948),
        tertiaryContainer = Color(0xFF523F5F),
        onTertiaryContainer = Color(0xFFF2DAFF),

        background = Color(0xFF111316),
        onBackground = Color(0xFFE2E2E6),
        surface = Color(0xFF111316),
        onSurface = Color(0xFFE2E2E6),
        surfaceVariant = Color(0xFF43474E),
        onSurfaceVariant = Color(0xFFC3C7CF),
        surfaceContainer = Color(0xFF1D2024),
        surfaceContainerLow = Color(0xFF1A1C1E),
        surfaceContainerLowest = Color(0xFF0C0E10),
        surfaceContainerHigh = Color(0xFF272A2E),
        surfaceContainerHighest = Color(0xFF323539),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        outline = Color(0xFF8D9199),
        outlineVariant = Color(0xFF43474E),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE2E2E6),
        inverseOnSurface = Color(0xFF2F3033),
        inversePrimary = Color(0xFF0061A4),

        surfaceBright = Color(0xFF37393C),
        surfaceDim = Color(0xFF111316),
        surfaceTint = Color(0xFF9ECAFF)
    )

    val GreenLight = lightColorScheme(
        primary = Color(0xFF006E1C),           // Forest green
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF97F593),   // Bright lime
        onPrimaryContainer = Color(0xFF002204),

        secondary = Color(0xFF52634F),          // Sage green
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD5E8CF),
        onSecondaryContainer = Color(0xFF101F0F),

        tertiary = Color(0xFF38656A),           // Teal accent
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFBCEBF1),
        onTertiaryContainer = Color(0xFF002023),

        background = Color(0xFFFCFDF6),
        onBackground = Color(0xFF1A1C18),
        surface = Color(0xFFFCFDF6),
        onSurface = Color(0xFF1A1C18),
        surfaceVariant = Color(0xFFDEE5D8),
        onSurfaceVariant = Color(0xFF424940),
        surfaceContainer = Color(0xFFF0F7EA),
        surfaceContainerLow = Color(0xFFF8FFED),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerHigh = Color(0xFFEAF1E4),
        surfaceContainerHighest = Color(0xFFE4EBDE),

        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        outline = Color(0xFF72796F),
        outlineVariant = Color(0xFFC2C9BC),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2F312D),
        inverseOnSurface = Color(0xFFF0F1EB),
        inversePrimary = Color(0xFF7BD87A),

        surfaceBright = Color(0xFFFCFDF6),
        surfaceDim = Color(0xFFDCDDD7),
        surfaceTint = Color(0xFF006E1C)
    )
    val GreenDark = darkColorScheme(
        primary = Color(0xFF7BD87A),
        onPrimary = Color(0xFF003909),
        primaryContainer = Color(0xFF005313),
        onPrimaryContainer = Color(0xFF97F593),

        secondary = Color(0xFFB9CCB4),
        onSecondary = Color(0xFF253423),
        secondaryContainer = Color(0xFF3B4B39),
        onSecondaryContainer = Color(0xFFD5E8CF),

        tertiary = Color(0xFFA0CFD4),
        onTertiary = Color(0xFF003639),
        tertiaryContainer = Color(0xFF1E4D51),
        onTertiaryContainer = Color(0xFFBCEBF1),

        background = Color(0xFF12140F),
        onBackground = Color(0xFFE2E3DD),
        surface = Color(0xFF12140F),
        onSurface = Color(0xFFE2E3DD),
        surfaceVariant = Color(0xFF424940),
        onSurfaceVariant = Color(0xFFC2C9BC),
        surfaceContainer = Color(0xFF1E201B),
        surfaceContainerLow = Color(0xFF1A1C18),
        surfaceContainerLowest = Color(0xFF0D0F0A),
        surfaceContainerHigh = Color(0xFF282A25),
        surfaceContainerHighest = Color(0xFF33352F),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        outline = Color(0xFF8C9388),
        outlineVariant = Color(0xFF424940),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE2E3DD),
        inverseOnSurface = Color(0xFF2F312D),
        inversePrimary = Color(0xFF006E1C),

        surfaceBright = Color(0xFF383935),
        surfaceDim = Color(0xFF12140F),
        surfaceTint = Color(0xFF7BD87A)
    )

    val OrangeLight = lightColorScheme(
        primary = Color(0xFF8C4A00),           // Burnt orange
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDCC2),   // Peach
        onPrimaryContainer = Color(0xFF2E1500),

        secondary = Color(0xFF765847),          // Brown
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFDCC2),
        onSecondaryContainer = Color(0xFF2B1708),

        tertiary = Color(0xFF5D5E2F),           // Olive accent
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFE2E2A8),
        onTertiaryContainer = Color(0xFF1A1C00),

        background = Color(0xFFFFFBFF),
        onBackground = Color(0xFF201A17),
        surface = Color(0xFFFFFBFF),
        onSurface = Color(0xFF201A17),
        surfaceVariant = Color(0xFFF0E0D0),
        onSurfaceVariant = Color(0xFF50453A),
        surfaceContainer = Color(0xFFFFF1E4),
        surfaceContainerLow = Color(0xFFFFFAF5),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerHigh = Color(0xFFF9EBDE),
        surfaceContainerHighest = Color(0xFFF3E5D8),

        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        outline = Color(0xFF81756A),
        outlineVariant = Color(0xFFD3C4B4),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF352F2B),
        inverseOnSurface = Color(0xFFFBEEE7),
        inversePrimary = Color(0xFFFFB86E),

        surfaceBright = Color(0xFFFFFBFF),
        surfaceDim = Color(0xFFE0D9D0),
        surfaceTint = Color(0xFF8C4A00)
    )
    val OrangeDark = darkColorScheme(
        primary = Color(0xFFFFB86E),
        onPrimary = Color(0xFF4A2800),
        primaryContainer = Color(0xFF6A3600),
        onPrimaryContainer = Color(0xFFFFDCC2),

        secondary = Color(0xFFE4BFA6),
        onSecondary = Color(0xFF422C1C),
        secondaryContainer = Color(0xFF5A4131),
        onSecondaryContainer = Color(0xFFFFDCC2),

        tertiary = Color(0xFFC6C68D),
        onTertiary = Color(0xFF2F3104),
        tertiaryContainer = Color(0xFF454619),
        onTertiaryContainer = Color(0xFFE2E2A8),

        background = Color(0xFF181210),
        onBackground = Color(0xFFF0DDD1),
        surface = Color(0xFF181210),
        onSurface = Color(0xFFF0DDD1),
        surfaceVariant = Color(0xFF50453A),
        onSurfaceVariant = Color(0xFFD3C4B4),
        surfaceContainer = Color(0xFF241E18),
        surfaceContainerLow = Color(0xFF201A17),
        surfaceContainerLowest = Color(0xFF130E0A),
        surfaceContainerHigh = Color(0xFF2E2822),
        surfaceContainerHighest = Color(0xFF39332D),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        outline = Color(0xFF9C8F83),
        outlineVariant = Color(0xFF50453A),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFF0DDD1),
        inverseOnSurface = Color(0xFF352F2B),
        inversePrimary = Color(0xFF8C4A00),

        surfaceBright = Color(0xFF3E3731),
        surfaceDim = Color(0xFF181210),
        surfaceTint = Color(0xFFFFB86E)
    )

    val PurpleLight = lightColorScheme(
        primary = Color(0xFF6750A4),           // Royal purple
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFEADDFF),   // Lavender
        onPrimaryContainer = Color(0xFF21005D),

        secondary = Color(0xFF625B71),          // Muted purple
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE8DEF8),
        onSecondaryContainer = Color(0xFF1D192B),

        tertiary = Color(0xFF7D5260),           // Rose accent
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD8E4),
        onTertiaryContainer = Color(0xFF31111D),

        background = Color(0xFFFFFBFE),
        onBackground = Color(0xFF1C1B1F),
        surface = Color(0xFFFFFBFE),
        onSurface = Color(0xFF1C1B1F),
        surfaceVariant = Color(0xFFE7E0EC),
        onSurfaceVariant = Color(0xFF49454F),
        surfaceContainer = Color(0xFFF3EDF7),
        surfaceContainerLow = Color(0xFFFEF7FF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerHigh = Color(0xFFECE6F0),
        surfaceContainerHighest = Color(0xFFE6E0E9),

        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        outline = Color(0xFF79747E),
        outlineVariant = Color(0xFFCAC4D0),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF313033),
        inverseOnSurface = Color(0xFFF4EFF4),
        inversePrimary = Color(0xFFD0BCFF),

        surfaceBright = Color(0xFFFFFBFE),
        surfaceDim = Color(0xFFDDD8DD),
        surfaceTint = Color(0xFF6750A4)
    )
    val PurpleDark = darkColorScheme(
        primary = Color(0xFFD0BCFF),
        onPrimary = Color(0xFF381E72),
        primaryContainer = Color(0xFF4F378B),
        onPrimaryContainer = Color(0xFFEADDFF),

        secondary = Color(0xFFCCC2DC),
        onSecondary = Color(0xFF332D41),
        secondaryContainer = Color(0xFF4A4458),
        onSecondaryContainer = Color(0xFFE8DEF8),

        tertiary = Color(0xFFEFB8C8),
        onTertiary = Color(0xFF492532),
        tertiaryContainer = Color(0xFF633B48),
        onTertiaryContainer = Color(0xFFFFD8E4),

        background = Color(0xFF141218),
        onBackground = Color(0xFFE6E1E5),
        surface = Color(0xFF141218),
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF49454F),
        onSurfaceVariant = Color(0xFFCAC4D0),
        surfaceContainer = Color(0xFF211F26),
        surfaceContainerLow = Color(0xFF1C1B1F),
        surfaceContainerLowest = Color(0xFF0F0D13),
        surfaceContainerHigh = Color(0xFF2B2930),
        surfaceContainerHighest = Color(0xFF36343B),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        outline = Color(0xFF938F99),
        outlineVariant = Color(0xFF49454F),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE6E1E5),
        inverseOnSurface = Color(0xFF313033),
        inversePrimary = Color(0xFF6750A4),

        surfaceBright = Color(0xFF3B383E),
        surfaceDim = Color(0xFF141218),
        surfaceTint = Color(0xFFD0BCFF)
    )

    val TealLight = lightColorScheme(
        primary = Color(0xFF006A6B),           // Deep teal
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF97F0F0),   // Aqua
        onPrimaryContainer = Color(0xFF002020),

        secondary = Color(0xFF4A6363),          // Blue-gray
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFCCE8E7),
        onSecondaryContainer = Color(0xFF051F1F),

        tertiary = Color(0xFF4D6042),           // Forest accent
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFCFE5BE),
        onTertiaryContainer = Color(0xFF0A1D05),

        background = Color(0xFFFAFDFD),
        onBackground = Color(0xFF161D1D),
        surface = Color(0xFFFAFDFD),
        onSurface = Color(0xFF161D1D),
        surfaceVariant = Color(0xFFDAE5E4),
        onSurfaceVariant = Color(0xFF3F4948),
        surfaceContainer = Color(0xFFEEF7F6),
        surfaceContainerLow = Color(0xFFF4FDFC),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerHigh = Color(0xFFE8F1F0),
        surfaceContainerHighest = Color(0xFFE2EBEA),

        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        outline = Color(0xFF6F7978),
        outlineVariant = Color(0xFFBEC9C8),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2B3231),
        inverseOnSurface = Color(0xFFECF2F1),
        inversePrimary = Color(0xFF4FD8D9),

        surfaceBright = Color(0xFFFAFDFD),
        surfaceDim = Color(0xFFDADBD7),
        surfaceTint = Color(0xFF006A6B)
    )
    val TealDark = darkColorScheme(
        primary = Color(0xFF4FD8D9),
        onPrimary = Color(0xFF003738),
        primaryContainer = Color(0xFF004F50),
        onPrimaryContainer = Color(0xFF97F0F0),

        secondary = Color(0xFFB0CCCA),
        onSecondary = Color(0xFF1B3534),
        secondaryContainer = Color(0xFF324B4A),
        onSecondaryContainer = Color(0xFFCCE8E7),

        tertiary = Color(0xFFB3C9A4),
        onTertiary = Color(0xFF1F3119),
        tertiaryContainer = Color(0xFF35482D),
        onTertiaryContainer = Color(0xFFCFE5BE),

        background = Color(0xFF0E1515),
        onBackground = Color(0xFFDEE3E3),
        surface = Color(0xFF0E1515),
        onSurface = Color(0xFFDEE3E3),
        surfaceVariant = Color(0xFF3F4948),
        onSurfaceVariant = Color(0xFFBEC9C8),
        surfaceContainer = Color(0xFF1A2120),
        surfaceContainerLow = Color(0xFF161D1D),
        surfaceContainerLowest = Color(0xFF090F0F),
        surfaceContainerHigh = Color(0xFF242B2A),
        surfaceContainerHighest = Color(0xFF2F3635),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        outline = Color(0xFF889392),
        outlineVariant = Color(0xFF3F4948),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFDEE3E3),
        inverseOnSurface = Color(0xFF2B3231),
        inversePrimary = Color(0xFF006A6B),

        surfaceBright = Color(0xFF343B3A),
        surfaceDim = Color(0xFF0E1515),
        surfaceTint = Color(0xFF4FD8D9)
    )
}