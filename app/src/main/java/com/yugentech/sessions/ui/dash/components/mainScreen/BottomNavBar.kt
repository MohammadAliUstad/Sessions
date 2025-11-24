package com.yugentech.sessions.ui.dash.components.mainScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.theme.tokens.corners

@Composable
fun BottomNavBar(
    items: List<AppScreens>,
    currentScreen: AppScreens,
    onSelected: (AppScreens) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.clip(
            RoundedCornerShape(
                topStart = MaterialTheme.corners.large,
                topEnd = MaterialTheme.corners.large
            )
        )
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentScreen == screen,
                onClick = { onSelected(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    val labelStyle =
                        if (currentScreen == screen) {
                            MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            MaterialTheme.typography.labelSmall
                        }

                    Text(
                        text = screen.title,
                        style = labelStyle
                    )
                }
                ,
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}