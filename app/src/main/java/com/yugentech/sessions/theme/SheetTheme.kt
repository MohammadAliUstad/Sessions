package com.yugentech.sessions.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Whether AMOLED black is showing right now (it's on and the app is dark). SessionsTheme
// provides it.
internal val LocalAmoledActive = staticCompositionLocalOf { false }

/**
 * Wrap a bottom sheet in this. With AMOLED black showing, the sheet goes black like the screens
 * behind it. Otherwise it changes nothing.
 */
@Composable
fun SheetTheme(content: @Composable () -> Unit) {
    if (LocalAmoledActive.current) {
        MaterialTheme(
            // surfaceContainerLow is the sheet's container colour
            colorScheme = MaterialTheme.colorScheme.copy(surfaceContainerLow = Color.Black),
            content = content
        )
    } else {
        content()
    }
}
