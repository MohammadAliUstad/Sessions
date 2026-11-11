package com.yugentech.sessions.ui.config.attributionsScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.spacing

/**
 * The credit cards, paged edge to edge: [contentPadding] insets the cards so they line up with
 * the rest of the screen at rest, while the pager itself spans the full width. Paging dots as on
 * the Discover carousel.
 */
@Composable
fun AttributionCarousel(contentPadding: PaddingValues = PaddingValues(0.dp)) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    // The cards differ in height, and a pager takes the height of the pages on screen, so it
    // would grow and shrink mid-swipe and shift everything below. Both pages are measured up
    // front (beyondViewportPageCount), the tallest is kept, and the pager holds that height
    // with every card filling it.
    val density = LocalDensity.current
    var tallest by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (tallest > 0) Modifier.height(with(density) { tallest.toDp() }) else Modifier),
            contentPadding = contentPadding,
            pageSpacing = MaterialTheme.spacing.m,
            beyondViewportPageCount = 1,
            verticalAlignment = Alignment.Top
        ) { page ->
            Box(modifier = Modifier.onSizeChanged { tallest = maxOf(tallest, it.height) }) {
                when (page) {
                    0 -> DesignCreditCard(modifier = Modifier.fillMaxHeight())
                    1 -> LordiconCreditCard(modifier = Modifier.fillMaxHeight())
                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

        // The Discover carousel's dots: the current one stretches into a pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { index ->
                val isSelected = pagerState.currentPage == index

                val animatedWidth by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "dot_width"
                )
                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    label = "dot_color"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(8.dp)
                        .width(animatedWidth)
                        .clip(CircleShape)
                        .background(animatedColor)
                )
            }
        }
    }
}
