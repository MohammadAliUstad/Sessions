package com.yugentech.sessions.announcements.catalog

import com.yugentech.sessions.R
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
            title = R.string.announcement_v6_title,
            message = R.string.announcement_v6_message,
            icon = Icons.Default.AutoAwesome,
            primaryAction = AnnouncementAction.OpenWhatsNew,
            primaryActionLabel = R.string.announcement_see_whats_new,
            dismissLabel = R.string.announcement_maybe_later
        ),
        Announcement(
            id = "v6_0_0_layouts_tip",
            minVersionCode = 27,
            maxVersionCode = 27,
            priority = 50,
            style = AnnouncementStyle.DIALOG,
            title = R.string.announcement_layouts_title,
            message = R.string.announcement_layouts_message,
            icon = Icons.Default.SpaceDashboard,
            primaryAction = AnnouncementAction.OpenRoute(AppScreen.HomeCustomization.route),
            primaryActionLabel = R.string.announcement_customize_now,
            dismissLabel = R.string.announcement_got_it
        )
    )
}
