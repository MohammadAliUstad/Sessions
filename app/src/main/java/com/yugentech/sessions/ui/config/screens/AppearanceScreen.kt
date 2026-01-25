package com.yugentech.sessions.ui.config.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.appearanceScreen.AmoledThemeSelector
import com.yugentech.sessions.ui.config.components.appearanceScreen.FontSelector
import com.yugentech.sessions.ui.config.components.appearanceScreen.ThemeColorSelector
import com.yugentech.sessions.ui.config.components.appearanceScreen.ThemeModeSelector

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onNavigateBack: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Appearance")
                        Text(
                            "Choose your preferred look",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = MaterialTheme.spacing.xxl,
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ThemeModeSelector(themeViewModel = themeViewModel)
            }

            item {
                AmoledThemeSelector(viewModel = themeViewModel)
            }

            item {
                FontSelector(viewModel = themeViewModel)
            }

            item {
                ThemeColorSelector(viewModel = themeViewModel)
            }
        }
    }
}