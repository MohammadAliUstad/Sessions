package com.yugentech.sessions.ui.config.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.net.toUri
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.aboutScreen.AppInfoCard
import com.yugentech.sessions.ui.config.components.aboutScreen.DonationDialog
import com.yugentech.sessions.ui.config.models.about.AboutContent
import com.yugentech.sessions.ui.dash.common.SectionHeader
import com.yugentech.sessions.ui.dash.components.settingsScreen.SettingsListItem
import com.yugentech.sessions.utils.AppConstants
import com.yugentech.sessions.utils.BillingManager
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    billingManager: BillingManager = koinInject()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val layoutDirection = LocalLayoutDirection.current
    var showDonationDialog by remember { mutableStateOf(false) }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        billingManager.startConnection()
    }

    LaunchedEffect(Unit) {
        billingManager.purchaseEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val supportItems = remember(context) {
        AboutContent.getSupportItems(
            context = context,
            onDonateClick = { showDonationDialog = true }
        )
    }

    val communityItems = remember(context) {
        AboutContent.getCommunityItems(context)
    }

    val legalItems = remember(context) {
        AboutContent.getLegalItems(
            context = context,
            onNavigateToLicenses = onNavigateToLicenses
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("About")
                        Text(
                            "App information and credits",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs),

            contentPadding = PaddingValues(
                top = scaffoldPadding.calculateTopPadding(),
                bottom = navBarPadding.calculateBottomPadding() + MaterialTheme.spacing.l,
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(
                    layoutDirection
                ),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            )
        ) {
            item { AppInfoCard() }

            item { SectionHeader(Icons.Filled.Favorite, "Connect & Support") }
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

            item { SectionHeader(Icons.Filled.ThumbUp, "Spread the Word") }
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

            item { SectionHeader(Icons.Filled.Info, "Legal") }
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

        if (showDonationDialog) {
            DonationDialog(
                onDismiss = { showDonationDialog = false },
                onGooglePlayClick = {
                    if (activity != null) {
                        billingManager.launchPurchaseFlow(activity, "donation_coffee")
                    }
                    showDonationDialog = false
                },
                onWebClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, AppConstants.KOFI_URL.toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                    }
                    showDonationDialog = false
                }
            )
        }
    }
}