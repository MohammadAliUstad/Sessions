package com.yugentech.sessions.announcements.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.yugentech.sessions.R

/**
 * Kind of presentation for an update announcement.
 */
enum class AnnouncementStyle {
    /** High-priority, modal dialog requiring user acknowledgement or action. */
    DIALOG,
    /** Low-intrusion, auto-dismissing toast across the top of the dashboard. */
    MESSAGE
}

/**
 * Actions that can be performed when the user taps the primary CTA.
 */
sealed interface AnnouncementAction {
    /** Simply dismiss and mark seen. */
    data object Dismiss : AnnouncementAction

    /** Navigate to the full What's New screen. */
    data object OpenWhatsNew : AnnouncementAction

    /** Open the app's Google Play Store listing. */
    data object OpenPlayStore : AnnouncementAction

    /** Open an external web URL. */
    data class OpenUrl(val url: String) : AnnouncementAction

    /** Navigate to an in-app route (e.g. AppScreen.Appearance.route). */
    data class OpenRoute(val route: String) : AnnouncementAction
}

/**
 * Definition of a message or dialog to show to users after an update.
 *
 * @property id Unique identifier for this specific announcement (e.g. "v6_0_0_studio_layout").
 * @property minVersionCode Minimum version code for which this announcement is valid (inclusive).
 * @property maxVersionCode Optional maximum version code (inclusive). Defaults to [minVersionCode].
 * @property priority Higher numbers are shown first when multiple announcements are pending.
 * @property style Whether this is a blocking [AnnouncementStyle.DIALOG] or brief [AnnouncementStyle.MESSAGE].
 * @property title Heading for the announcement.
 * @property message Explanatory body text.
 * @property icon Optional vector icon rendered at the top of the dialog.
 * @property primaryAction Action performed on primary button click.
 * @property primaryActionLabel Text for the primary button. Defaults to "Explore" or "OK".
 * @property dismissLabel Text for the secondary/dismiss button on dialogs. If null, only primary action is shown.
 */
data class Announcement(
    val id: String,
    val minVersionCode: Int,
    val maxVersionCode: Int = minVersionCode,
    val priority: Int = 0,
    val style: AnnouncementStyle = AnnouncementStyle.DIALOG,
    @StringRes val title: Int,
    @StringRes val message: Int,
    val icon: ImageVector? = null,
    val primaryAction: AnnouncementAction = AnnouncementAction.Dismiss,
    @StringRes val primaryActionLabel: Int = R.string.announcement_got_it,
    @StringRes val dismissLabel: Int? = null
)
