package com.yugentech.sessions.ui.config.attributionsScreen

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.attributionsScreen.components.AttributionCarousel // Import the carousel
import com.yugentech.sessions.ui.config.attributionsScreen.components.AttributionsTopBar
import com.yugentech.sessions.ui.config.attributionsScreen.components.LibraryItem
import com.yugentech.sessions.ui.config.attributionsScreen.components.OpenSourceCard
import com.yugentech.sessions.ui.config.model.license.LicensesContent
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape
import com.yugentech.sessions.utils.AppConstants.GITHUB_URL
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributionsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val libraries = remember { LicensesContent.libraries }
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AttributionsTopBar(
                scrollBehavior = scrollBehavior,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
        // The side inset each item takes itself, so the carousel can run edge to edge
        val sidePadding = PaddingValues(
            start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
            end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                top = MaterialTheme.spacing.m,
                bottom = navBarPadding.calculateBottomPadding() + 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            item {
                Box(Modifier.padding(sidePadding)) { OpenSourceCard(githubUrl = GITHUB_URL) }
            }

            // Inset inside the pager instead, so the cards line up with the rest at rest but
            // slide out to the screen edges while scrolling
            item { AttributionCarousel(contentPadding = sidePadding) }

            item {
                Box(Modifier.padding(sidePadding)) {
                    SectionHeader(
                        title = stringResource(R.string.attributions_libraries),
                        icon = Icons.Default.IntegrationInstructions
                    )
                }
            }

            itemsIndexed(libraries) { index, lib ->
                Box(Modifier.padding(sidePadding)) {
                    LibraryItem(
                        lib = lib,
                        shape = itemShape(index, libraries.size)
                    )
                }
            }
        }
    }
}