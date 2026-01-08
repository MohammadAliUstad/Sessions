package com.yugentech.sessions.ui.config.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.aboutScreen.AppInfoCard
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsListItem
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSectionHeader

// Data class to hold item details
private data class AboutOption(
    val title: String,
    val subtitle: String?,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    // --- Actions ---

    // 1. Support & Feedback
    val supportItems = listOf(
        AboutOption(
            title = "Contact Developer",
            subtitle = "Get in touch for support or feedback",
            icon = Icons.Default.Email,
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:${context.getString(R.string.mail)}".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_sub))
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        ),
        AboutOption(
            title = "Buy me a coffee",
            subtitle = "Support the development",
            icon = Icons.Default.LocalCafe,
            onClick = {
                val urlIntent =
                    Intent(Intent.ACTION_VIEW, "https://buymeacoffee.com/yourusername".toUri())
                try {
                    context.startActivity(urlIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        ),
        AboutOption(
            title = "Visit GitHub",
            subtitle = "View source code and contribute",
            icon = Icons.Default.Code,
            onClick = {
                val urlIntent =
                    Intent(Intent.ACTION_VIEW, context.getString(R.string.github).toUri())
                context.startActivity(urlIntent)
            }
        )
    )

    // 2. Spread the Word
    val communityItems = listOf(
        AboutOption(
            title = "Rate this App",
            subtitle = "Leave a review on the Play Store",
            icon = Icons.Default.StarRate,
            onClick = {
                // Opens Play Store listing
                val appPackageName = context.packageName
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "market://details?id=$appPackageName".toUri()
                        )
                    )
                } catch (e: android.content.ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                        )
                    )
                }
            }
        ),
        AboutOption(
            title = "Share with Friends",
            subtitle = "Help others focus better",
            icon = Icons.Default.Share,
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out Sessions, a minimal focus timer app: https://play.google.com/store/apps/details?id=${context.packageName}"
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        )
    )

    // 3. Legal
    val legalItems = listOf(
        AboutOption(
            title = "Privacy Policy",
            subtitle = null, // Subtitle often not needed for legal items
            icon = Icons.Default.Policy,
            onClick = {
                val urlIntent =
                    Intent(Intent.ACTION_VIEW, "https://your-privacy-policy-url.com".toUri())
                try {
                    context.startActivity(urlIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        ),
        AboutOption(
            title = "Terms of Service",
            subtitle = null,
            icon = Icons.Default.Gavel,
            onClick = {
                val urlIntent = Intent(Intent.ACTION_VIEW, "https://your-terms-url.com".toUri())
                try {
                    context.startActivity(urlIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        ),
        AboutOption(
            title = "Open Source Licenses",
            subtitle = "Software used in this app",
            icon = Icons.Default.Description,
            onClick = {
                // Typically connects to OssLicensesMenuActivity if using the GMS plugin
                // For now, we can leave it as a placeholder or link to a file
            }
        )
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(
                top = scaffoldPadding.calculateTopPadding(),
                bottom = navBarPadding.calculateBottomPadding() + MaterialTheme.spacing.l,
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(
                    layoutDirection
                ),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            )
        ) {
            // --- HEADER ---
            item {
                AppInfoCard()
            }

            // --- SECTION 1: Connect ---
            item {
                SettingsSectionHeader(
                    icon = Icons.Filled.Favorite,
                    title = "Connect & Support"
                )
            }
            itemsIndexed(supportItems) { index, item ->
                SettingsListItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    leadingIcon = item.icon,
                    index = index,
                    totalCount = supportItems.size,
                    onClick = item.onClick
                )
            }

            // --- SECTION 2: Growth ---
            item {
                SettingsSectionHeader(
                    icon = Icons.Filled.ThumbUp,
                    title = "Spread the Word"
                )
            }
            itemsIndexed(communityItems) { index, item ->
                SettingsListItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    leadingIcon = item.icon,
                    index = index,
                    totalCount = communityItems.size,
                    onClick = item.onClick
                )
            }

            // --- SECTION 3: Legal ---
            item {
                SettingsSectionHeader(
                    icon = Icons.Filled.Info,
                    title = "Legal"
                )
            }
            itemsIndexed(legalItems) { index, item ->
                SettingsListItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    leadingIcon = item.icon,
                    index = index,
                    totalCount = legalItems.size,
                    onClick = item.onClick
                )
            }
        }
    }
}