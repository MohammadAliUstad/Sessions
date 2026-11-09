package com.yugentech.sessions.ui.config.appearanceScreen

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.ui.config.appearanceScreen.components.CardAnimationSelector
import com.yugentech.sessions.ui.config.appearanceScreen.components.CardStyleSelector
import com.yugentech.sessions.ui.config.appearanceScreen.components.HomeGradientSelector
import com.yugentech.sessions.ui.config.appearanceScreen.components.HomeLayoutSelector
import com.yugentech.sessions.ui.config.appearanceScreen.components.SessionCardPreview

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeCustomizationScreen(
    onNavigateBack: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.settings_home_cards))
                        Text(
                            stringResource(R.string.settings_home_cards_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                bottom = navBarPadding.calculateBottomPadding(),
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { HomeLayoutSelector(viewModel = themeViewModel) }
            item {
                HomeGradientSelector(
                    viewModel = themeViewModel,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.s)
                )
            }
            item { SessionCardPreview(viewModel = themeViewModel) }
            item { CardStyleSelector(viewModel = themeViewModel) }
            item { CardAnimationSelector(viewModel = themeViewModel) }
        }
    }
}
