package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

/**
 * The transparent top bar of the main pages. The same code in Sessions and Ryori (only the
 * package differs), so their bars look and behave as mirrors of each other.
 *
 * Transparent at rest so the page shows through; once content scrolls under it, a soft scrim
 * fades in from the top, as Quill's Library does. [scrollBehavior] tells it when content is
 * under it (the page must be wired to its nestedScrollConnection).
 */
@Composable
fun ScrimTopAppBar(
    title: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val surface = MaterialTheme.colorScheme.surface
    val scrolledUnder = (scrollBehavior?.state?.overlappedFraction ?: 0f) > 0.01f
    val scrimAlpha by animateFloatAsState(if (scrolledUnder) 1f else 0f, tween(300), label = "topBarScrim")

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = scrimAlpha }
                .background(rememberSmoothScrimBrush(surface))
        )

        TopAppBar(
            title = title,
            windowInsets = WindowInsets.statusBars,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            ),
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    }
}
