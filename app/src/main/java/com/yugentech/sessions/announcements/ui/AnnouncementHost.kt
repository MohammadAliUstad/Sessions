package com.yugentech.sessions.announcements.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import com.yugentech.sessions.announcements.model.AnnouncementAction
import com.yugentech.sessions.announcements.model.AnnouncementStyle
import com.yugentech.sessions.announcements.viewmodel.AnnouncementViewModel
import com.yugentech.sessions.ui.dash.mainScreen.components.ToastMessage
import com.yugentech.sessions.utils.AppConstants
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
 * Host composable that listens for pending update announcements and renders them appropriately.
 *
 * @param isTimerIdle When false (timer running/paused), announcements will hold back until the user finishes.
 * @param onOpenWhatsNew Callback to navigate to the What's New screen.
 * @param onNavigateRoute Callback to navigate to an arbitrary in-app route.
 */
@Composable
fun AnnouncementHost(
    isTimerIdle: Boolean,
    onOpenWhatsNew: () -> Unit,
    onNavigateRoute: (String) -> Unit,
    viewModel: AnnouncementViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Check for announcements shortly after the screen opens, once the timer is idle,
    // and after initial permission requests have had a chance to show.
    LaunchedEffect(isTimerIdle) {
        if (isTimerIdle) {
            delay(1200)
            viewModel.checkForAnnouncements()
        }
    }

    // Only while Home is the screen on top. Its screen stays composed under the one opened from
    // a dialog (What's New) and is drawn again as that one's back gesture previews it; a dialog
    // is its own window, so showing the next one then would pop it over What's New and cancel
    // the back gesture, leaving the user stuck there. Waiting for RESUMED holds it back until
    // the back navigation has actually landed on Home.
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    if (!lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) return

    val current = state.current ?: return

    when (current.style) {
        AnnouncementStyle.DIALOG -> {
            AnnouncementDialog(
                announcement = current,
                onPrimaryClick = {
                    handleAction(
                        action = current.primaryAction,
                        context = context,
                        onOpenWhatsNew = onOpenWhatsNew,
                        onNavigateRoute = onNavigateRoute
                    )
                    viewModel.dismissCurrent()
                },
                onDismiss = {
                    viewModel.dismissCurrent()
                }
            )
        }

        AnnouncementStyle.MESSAGE -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ToastMessage(
                    message = current.title + if (current.message.isNotBlank()) ": ${current.message}" else "",
                    onDismiss = { viewModel.dismissCurrent() },
                    duration = 5000L,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .zIndex(5f)
                )
            }
        }
    }
}

private fun handleAction(
    action: AnnouncementAction,
    context: Context,
    onOpenWhatsNew: () -> Unit,
    onNavigateRoute: (String) -> Unit
) {
    when (action) {
        AnnouncementAction.Dismiss -> Unit

        AnnouncementAction.OpenWhatsNew -> onOpenWhatsNew()

        AnnouncementAction.OpenPlayStore -> openPlayStore(context)

        is AnnouncementAction.OpenUrl -> {
            try {
                val intent = Intent(Intent.ACTION_VIEW, action.url.toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Timber.e(e, "Failed to open URL: ${action.url}")
            }
        }

        is AnnouncementAction.OpenRoute -> onNavigateRoute(action.route)
    }
}

private fun openPlayStore(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, AppConstants.MARKET_URL.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                AppConstants.PLAY_STORE_URL.toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
