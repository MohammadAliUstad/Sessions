package com.yugentech.sessions.ui.config.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSectionHeader
import com.yugentech.sessions.ui.dash.components.common.itemShape

// Simplified model (License removed from constructor since we know it's Apache 2.0)
data class LibraryItem(
    val name: String,
    val author: String,
    val url: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    // --- YOUR TECH STACK ---
    val libraries = listOf(
        LibraryItem("Jetpack Compose", "Google", "https://developer.android.com/jetpack/compose"),
        LibraryItem("Kotlin", "JetBrains", "https://kotlinlang.org/"),
        LibraryItem("Koin", "InsertKoinIO", "https://insert-koin.io/"),
        LibraryItem("Timber", "Jake Wharton", "https://github.com/JakeWharton/timber"),
        LibraryItem("Accompanist", "Google", "https://github.com/google/accompanist"),
        LibraryItem("Retrofit", "Square", "https://square.github.io/retrofit/"),
        LibraryItem("Coroutines", "JetBrains", "https://github.com/Kotlin/kotlinx.coroutines"),
        LibraryItem("Material Design 3", "Google", "https://m3.material.io/")
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Licenses") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp) // Tight spacing for the grouped look
        ) {
            // --- SECTION 1: HERO CARD ---
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Code,
                                        null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Sessions is Open Source",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Licensed under Apache 2.0",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            "The source code is available on GitHub. You are free to view, learn from, and modify the code for personal use.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FilledTonalButton(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://github.com/mohammadaliustad/Sessions".toUri()
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        ) {
                            Text("View Source Code")
                        }
                    }
                }
            }

            // --- SECTION HEADER ---
            item {
                SettingsSectionHeader(
                    title = "Libraries We Use",
                    icon = Icons.Default.IntegrationInstructions
                )
            }

            // --- SECTION 2: LIBRARIES LIST (Modern Grouped Style) ---
            itemsIndexed(libraries) { index, lib ->
                val shape = itemShape(index, libraries.size)

                ListItem(
                    headlineContent = {
                        Text(
                            lib.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    supportingContent = {
                        Text(
                            lib.author,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, lib.url.toUri())
                            context.startActivity(intent)
                        },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent // Let the Box/Background handle the color
                    )
                )
            }
        }
    }
}