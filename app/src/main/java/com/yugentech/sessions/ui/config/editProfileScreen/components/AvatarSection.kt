package com.yugentech.sessions.ui.config.editProfileScreen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import kotlinx.coroutines.launch

// Category tabs over a grid of avatars (tap a tab to switch), drawn straight on the avatar sheet
// (no card around it): the tabs run edge to edge and the grid lines up with the sheet title.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AvatarSection(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember { AvatarCategory.entries }
    val scope = rememberCoroutineScope()

    val initialPage = remember(selectedAvatarId) {
        val category = AvatarRepository.getAvatarById(selectedAvatarId)?.category ?: categories.first()
        categories.indexOf(category)
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { categories.size }
    )

    // Every page is as tall as the fullest category, so the pager (and the sheet around it)
    // doesn't resize mid-swipe. Short pages are padded with empty rows of the same height.
    val maxRows = remember {
        categories.maxOf { (AvatarRepository.getAvatarsByCategory(it).size + 2) / 3 }
    }
    val labelHeight = with(LocalDensity.current) {
        MaterialTheme.typography.labelMedium.lineHeight.toDp()
    }
    val rowHeight = AvatarOptionSize + MaterialTheme.spacing.s + labelHeight

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
    ) {
        // The app theme uses expressive motion, whose springs overshoot: the tab indicator
        // bounced at the end of every switch. Standard motion slides it without the bounce.
        MaterialTheme(motionScheme = MotionScheme.standard()) {
            SecondaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 24.dp,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = MaterialTheme.corners.medium,
                                topEnd = MaterialTheme.corners.medium
                            )
                        ),
                        text = {
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }
        }

        // Categories change only by tapping a tab: swiping between pages hitched at the end.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false,
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            val category = categories[pageIndex]

            val categoryAvatars = remember(category) {
                AvatarRepository.getAvatarsByCategory(category)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = MaterialTheme.spacing.s),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
            ) {
                val rows = categoryAvatars.chunked(3)
                rows.forEach { rowAvatars ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowAvatars.forEach { avatar ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                AvatarOption(
                                    avatar = avatar,
                                    isSelected = selectedAvatarId == avatar.id,
                                    onSelect = { onAvatarSelected(avatar.id) }
                                )
                            }
                        }
                        // Keep a short last row's avatars in the same columns as the rows above.
                        repeat(3 - rowAvatars.size) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
                repeat(maxRows - rows.size) {
                    Spacer(modifier = Modifier.height(rowHeight))
                }
            }
        }
    }
}
