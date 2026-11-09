package com.yugentech.sessions.ui.config.model.about

import com.yugentech.sessions.R
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
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
        onDonateClick: () -> Unit,
        onMoreAppsClick: () -> Unit
    ): List<AboutOption> {
        return listOf(
            AboutOption(
                title = context.getString(R.string.contact_developer),
                subtitle = context.getString(R.string.about_contact_subtitle),
                icon = Icons.Default.Email,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO, AppConstants.SUPPORT_EMAIL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = context.getString(R.string.about_donate),
                subtitle = context.getString(R.string.about_donate_subtitle),
                icon = Icons.Default.LocalCafe,
                onClick = onDonateClick
            ),
            AboutOption(
                title = context.getString(R.string.visit_github),
                subtitle = context.getString(R.string.view_source_code_and_contribute),
                icon = Icons.Default.Code,
                onClick = {
                    val urlIntent = Intent(Intent.ACTION_VIEW, AppConstants.GITHUB_URL.toUri())
                    context.startActivity(urlIntent)
                }
            ),
            AboutOption(
                title = context.getString(R.string.settings_more_from_us),
                subtitle = context.getString(R.string.about_more_apps_subtitle),
                icon = Icons.Default.Apps,
                onClick = onMoreAppsClick
            )
        )
    }

    fun getCommunityItems(context: Context): List<AboutOption> {
        return listOf(
            AboutOption(
                title = context.getString(R.string.about_rate),
                subtitle = context.getString(R.string.about_rate_subtitle),
                icon = Icons.Default.StarRate,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, AppConstants.MARKET_URL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = context.getString(R.string.about_share),
                subtitle = context.getString(R.string.about_share_subtitle),
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
                title = context.getString(R.string.about_privacy),
                subtitle = null,
                icon = Icons.Default.Policy,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, AppConstants.PRIVACY_POLICY_URL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = context.getString(R.string.about_terms),
                subtitle = null,
                icon = Icons.Default.Gavel,
                onClick = {
                    val intent =
                        Intent(Intent.ACTION_VIEW, AppConstants.TERMS_OF_SERVICE_URL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = context.getString(R.string.attributions_title),
                subtitle = context.getString(R.string.about_attributions_subtitle),
                icon = Icons.Default.Description,
                onClick = onNavigateToLicenses
            )
        )
    }
}