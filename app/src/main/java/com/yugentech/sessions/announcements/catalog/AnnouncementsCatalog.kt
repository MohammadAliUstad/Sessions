package com.yugentech.sessions.announcements.catalog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SpaceDashboard
import com.yugentech.sessions.announcements.model.Announcement
import com.yugentech.sessions.announcements.model.AnnouncementAction
import com.yugentech.sessions.announcements.model.AnnouncementStyle
import com.yugentech.sessions.navigation.screen.AppScreen

/**
 * Central registry of all release announcements.
 *
 * Whenever you ship an update:
 * 1. Add your new [Announcement] entries to [ALL_ANNOUNCEMENTS].
 * 2. Set [Announcement.minVersionCode] to the new version code (or a range if relevant).
 * 3. Each announcement will be presented to existing users exactly once.
 *    Fresh installs automatically skip older announcements so new users aren't spammed.
 */
object AnnouncementsCatalog {

    /**
     * All registered announcements across releases.
     * Keep older announcements here as a permanent record; the system only shows
     * ones matching the current app version that haven't been seen yet.
     */
    val ALL_ANNOUNCEMENTS: List<Announcement> = listOf(
        // Version 6.0.0 (versionCode 27)
        Announcement(
            id = "v6_0_0_whats_new",
            minVersionCode = 27,
            maxVersionCode = 27,
            priority = 100,
            style = AnnouncementStyle.DIALOG,
            title = "What's New in Sessions 6.0",
            message = "We've added the new Studio layout with a watch-style dial, living glow backgrounds, mesh animations, and smoother transitions throughout the app.",
            icon = Icons.Default.AutoAwesome,
            primaryAction = AnnouncementAction.OpenWhatsNew,
            primaryActionLabel = "See What's New",
            dismissLabel = "Maybe Later"
        ),
        Announcement(
            id = "v6_0_0_layouts_tip",
            minVersionCode = 27,
            maxVersionCode = 27,
            priority = 50,
            style = AnnouncementStyle.DIALOG,
            title = "Try the New Layouts",
            message = "You can switch between Classic, Refined, and Studio layouts anytime under Settings > Home & Cards.",
            icon = Icons.Default.SpaceDashboard,
            primaryAction = AnnouncementAction.OpenRoute(AppScreen.HomeCustomization.route),
            primaryActionLabel = "Customize Now",
            dismissLabel = "Got It"
        )
    )
}
