package com.yugentech.sessions.ui.config.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.aboutScreen.AppInfoCard
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsListItem
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSectionHeader
import com.yugentech.sessions.utils.BillingManager
import org.koin.compose.koinInject

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
    onNavigateBack: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    billingManager: BillingManager = koinInject()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val layoutDirection = LocalLayoutDirection.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    // State for Donation Dialog
    var showDonationDialog by remember { mutableStateOf(false) }

    // 1. Start Connection when screen opens
    LaunchedEffect(Unit) {
        billingManager.startConnection()
    }

    // 2. Listen for Purchase Success Events
    LaunchedEffect(Unit) {
        billingManager.purchaseEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    // --- Actions Data ---
    val supportItems = listOf(
        AboutOption(
            title = "Contact Developer",
            subtitle = "Get in touch for support or feedback",
            icon = Icons.Default.Email,
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = context.getString(R.string.mail).toUri()
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
            onClick = { showDonationDialog = true }
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

    val communityItems = listOf(
        AboutOption(
            title = "Rate this App",
            subtitle = "Leave a review on the Play Store",
            icon = Icons.Default.StarRate,
            onClick = {
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
                        "Check out Sessions, a minimal pomodoro focus app\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        )
    )

    val legalItems = listOf(
        AboutOption(
            title = "Privacy Policy",
            subtitle = null,
            icon = Icons.Default.Policy,
            onClick = {
                val urlIntent = Intent(
                    Intent.ACTION_VIEW,
                    "https://sites.google.com/view/sessionsprivacypolicy/home".toUri()
                )
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
                val urlIntent = Intent(
                    Intent.ACTION_VIEW,
                    "https://sites.google.com/view/sessionstermsofservice/home".toUri()
                )
                try {
                    context.startActivity(urlIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        ),
        AboutOption(
            title = "Licenses",
            subtitle = "Libraries used to build Sessions",
            icon = Icons.Default.Description,
            onClick = onNavigateToLicenses
        )
    )

    // --- UI Structure ---
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
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            )
        ) {
            item { AppInfoCard() }

            // Section 1
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

            // Section 2
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

            // Section 3
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

        if (showDonationDialog) {
            DonationDialog(
                onDismiss = { showDonationDialog = false },
                onGooglePlayClick = {
                    if (activity != null) {
                        billingManager.launchPurchaseFlow(activity, "donation_coffee")
                        showDonationDialog = false
                    }

                    showDonationDialog = false
                },
                onWebClick = {
                    val kofiUrl = "https://ko-fi.com/yugentech"
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, kofiUrl.toUri())
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

// --- Dialog Components ---

@Composable
fun DonationDialog(
    onDismiss: () -> Unit,
    onGooglePlayClick: () -> Unit,
    onWebClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Coffee,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Fuel the Development",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = "Sessions is free and open source. If you enjoy the focus, consider supporting the journey!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                // Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Option 1: Google Play (Best for India/UPI)
                    PaymentOptionButton(
                        text = "Google Play (UPI / Cards)",
                        icon = Icons.Default.ShoppingBag,
                        onClick = onGooglePlayClick
                    )

                    // Option 2: Ko-fi (Best for International/PayPal)
                    PaymentOptionButton(
                        text = "Ko-fi (PayPal / International)",
                        icon = Icons.Default.Public,
                        onClick = onWebClick
                    )
                }

                // Footer
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Maybe Later",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentOptionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .alpha(0.5f)
        )
    }
}