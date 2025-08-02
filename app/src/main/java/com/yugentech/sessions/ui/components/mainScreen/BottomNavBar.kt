package com.yugentech.sessions.ui.components.mainScreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.navigation.AppScreens

@Composable
fun BottomNavBar(
    items: List<AppScreens>,
    currentScreen: AppScreens,
    onSelected: (AppScreens) -> Unit,
) {
    // ✅ FLOATING NAV BAR WITH SURFACE
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp) // ✅ Add padding for floating effect
            .clip(RoundedCornerShape(24.dp)), // ✅ Rounded corners for floating look
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp, // ✅ Higher elevation for floating effect
        shadowElevation = 8.dp // ✅ Shadow for depth
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp, // ✅ Remove nav bar elevation since Surface handles it
            windowInsets = WindowInsets(0) // ✅ Remove default insets since we handle padding
        ) {
            items.forEach { screen ->
                val selected = currentScreen == screen
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelected(screen) },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}