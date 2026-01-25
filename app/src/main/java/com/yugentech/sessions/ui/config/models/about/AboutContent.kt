package com.yugentech.sessions.ui.config.models.about

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.core.net.toUri
import com.yugentech.sessions.utils.AppConstants

object AboutContent {

    fun getSupportItems(
        context: Context,
        onDonateClick: () -> Unit
    ): List<AboutOption> {
        return listOf(
            AboutOption(
                title = "Contact Developer",
                subtitle = "Get in touch for support or feedback",
                icon = Icons.Default.Email,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO, AppConstants.SUPPORT_EMAIL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = "Buy me a coffee",
                subtitle = "Support the development",
                icon = Icons.Default.LocalCafe,
                onClick = onDonateClick
            ),
            AboutOption(
                title = "Visit GitHub",
                subtitle = "View source code and contribute",
                icon = Icons.Default.Code,
                onClick = {
                    val urlIntent = Intent(Intent.ACTION_VIEW, AppConstants.GITHUB_URL.toUri())
                    context.startActivity(urlIntent)
                }
            )
        )
    }

    fun getCommunityItems(context: Context): List<AboutOption> {
        return listOf(
            AboutOption(
                title = "Rate this App",
                subtitle = "Leave a review on the Play Store",
                icon = Icons.Default.StarRate,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, AppConstants.MARKET_URL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = "Share with Friends",
                subtitle = "Help others focus better",
                icon = Icons.Default.Share,
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, AppConstants.SHARE_MESSAGE)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
                }
            )
        )
    }

    fun getLegalItems(
        context: Context,
        onNavigateToLicenses: () -> Unit
    ): List<AboutOption> {
        return listOf(
            AboutOption(
                title = "Privacy Policy",
                subtitle = null,
                icon = Icons.Default.Policy,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, AppConstants.PRIVACY_POLICY_URL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = "Terms of Service",
                subtitle = null,
                icon = Icons.Default.Gavel,
                onClick = {
                    val intent =
                        Intent(Intent.ACTION_VIEW, AppConstants.TERMS_OF_SERVICE_URL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = "Licenses",
                subtitle = "Libraries used to build Sessions",
                icon = Icons.Default.Description,
                onClick = onNavigateToLicenses
            )
        )
    }
}