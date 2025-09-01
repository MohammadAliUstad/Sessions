package com.yugentech.sessions.ui.config.attributionsScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.attributionsScreen.components.AttributionsTopBar
import com.yugentech.sessions.ui.config.attributionsScreen.components.DesignCreditCard
import com.yugentech.sessions.ui.config.attributionsScreen.components.LibraryItem
import com.yugentech.sessions.ui.config.attributionsScreen.components.OpenSourceCard
import com.yugentech.sessions.ui.config.models.license.LicensesContent
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape
import com.yugentech.sessions.utils.AppConstants.GITHUB_URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributionsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val libraries = remember { LicensesContent.libraries }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AttributionsTopBar(
                scrollBehavior = scrollBehavior,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                top = MaterialTheme.spacing.m,
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m,
                bottom = MaterialTheme.spacing.xxl
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            item {
                OpenSourceCard(githubUrl = GITHUB_URL)
            }

            item {
                DesignCreditCard()
            }

            item {
                SectionHeader(
                    title = "Libraries We Use",
                    icon = Icons.Default.IntegrationInstructions
                )
            }

            itemsIndexed(libraries) { index, lib ->
                LibraryItem(
                    lib = lib,
                    shape = itemShape(index, libraries.size)
                )
            }
        }
    }
}