package com.yugentech.sessions.ui.config.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.net.toUri
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.aboutScreen.AboutContactCard
import com.yugentech.sessions.ui.config.components.aboutScreen.AppInfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPadding ->

        // Calculate bottom padding for navigation bar
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l),
            // FIX: Combine Scaffold padding (top) with WindowInsets (bottom)
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = scaffoldPadding.calculateTopPadding() + MaterialTheme.spacing.m,
                bottom = navBarPadding.calculateBottomPadding() + MaterialTheme.spacing.l,
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            )
        ) {
            item {
                AppInfoCard()
            }

            item {
                AboutContactCard(
                    onEmailClick = {
                        // UPDATE: New Email Address
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:yugentech.kazuki@gmail.com".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "Sessions App Feedback")
                        }
                        // Safety check in case no email client is installed
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    onGitHubClick = {
                        // UPDATE: New GitHub Repository Link
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/MohammadAliUstad/Sessions".toUri()
                        )
                        context.startActivity(urlIntent)
                    }
                )
            }
        }
    }
}