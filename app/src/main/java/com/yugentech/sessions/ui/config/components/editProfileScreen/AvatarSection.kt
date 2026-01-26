package com.yugentech.sessions.ui.config.components.editProfileScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AvatarSection(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit
) {
    val categories = remember { AvatarCategory.entries }
    val scope = rememberCoroutineScope()

    // Determine the initial page based on the currently selected avatar
    val initialPage = remember(selectedAvatarId) {
        val category = AvatarRepository.getAvatarById(selectedAvatarId)?.category ?: categories.first()
        categories.indexOf(category)
    }

    // Pager state controls which category is currently visible
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { categories.size }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(top = MaterialTheme.spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                SecondaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = MaterialTheme.spacing.m,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    categories.forEachIndexed { index, category ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                // Scroll pager to the clicked tab
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

            // HorizontalPager handles the swipe logic and page transitions
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val category = categories[pageIndex]

                // Fetch avatars for this specific page/category
                val categoryAvatars = remember(category) {
                    AvatarRepository.getAvatarsByCategory(category)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.l),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
                ) {
                    categoryAvatars.chunked(3).forEach { rowAvatars ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                        }
                    }
                }
            }
        }
    }
}