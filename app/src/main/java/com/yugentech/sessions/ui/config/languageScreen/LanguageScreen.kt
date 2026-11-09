package com.yugentech.sessions.ui.config.languageScreen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
import com.yugentech.sessions.localization.AppLanguage
import com.yugentech.sessions.localization.AppLocale
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape

/**
 * Settings > Language: "System default" (the phone's language, named beside it) and the ten
 * languages Sessions is translated into. Each shows its own name as written in that language,
 * so anyone can find theirs whatever the app is showing now, with its name in the current
 * language under it. Picking one switches the whole app straight away.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current
    var selected by remember { mutableStateOf(AppLocale.current(context)) }
    val systemLanguageName = remember {
        val locale = AppLocale.systemLocale()
        locale.getDisplayName(locale).replaceFirstChar { it.titlecase(locale) }
    }

    fun pick(language: AppLanguage?) {
        if (language == selected) return
        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        selected = language
        (context as? Activity)?.let { AppLocale.set(it, language) }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.language_title))
                        Text(
                            stringResource(R.string.language_subtitle),
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
                bottom = navBarPadding.calculateBottomPadding() + MaterialTheme.spacing.l,
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                SectionHeader(icon = Icons.Default.PhoneAndroid, title = stringResource(R.string.language_section_device))
            }
            item {
                LanguageRow(
                    title = stringResource(R.string.language_system_default),
                    subtitle = systemLanguageName,
                    selected = selected == null,
                    index = 0,
                    count = 1,
                    onClick = { pick(null) }
                )
            }

            item {
                SectionHeader(icon = Icons.Default.Translate, title = stringResource(R.string.language_section_languages))
            }
            itemsIndexed(AppLanguage.entries, key = { _, language -> language.tag }) { index, language ->
                LanguageRow(
                    title = language.nativeName,
                    subtitle = stringResource(language.displayName),
                    selected = selected == language,
                    index = index,
                    count = AppLanguage.entries.size,
                    onClick = { pick(language) }
                )
            }

            item {
                Spacer(Modifier.height(MaterialTheme.spacing.m))
                FooterNote(
                    icon = Icons.Default.Language,
                    text = stringResource(R.string.language_footer)
                )
            }
        }
    }
}

/**
 * One language, styled like Settings' rows (grouped shapes on the surface container). The
 * picked one takes the secondary container and a check in a filled circle.
 */
@Composable
private fun LanguageRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    index: Int,
    count: Int,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(
        if (selected) colors.secondaryContainer else colors.surfaceContainer,
        tween(250),
        label = "languageRow"
    )
    val ring by animateColorAsState(
        if (selected) colors.primary else colors.outline,
        tween(250),
        label = "languageRing"
    )
    ListItem(
        headlineContent = {
            Text(
                text = title,
                // A name in its own script reads in its own direction (Arabic right to left)
                // whatever direction the app is laid out in
                style = MaterialTheme.typography.titleMedium.copy(textDirection = TextDirection.Content),
                modifier = Modifier.padding(start = MaterialTheme.spacing.s, top = MaterialTheme.spacing.s)
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(start = MaterialTheme.spacing.s, bottom = MaterialTheme.spacing.s)
            )
        },
        trailingContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, ring, CircleShape)
            ) {
                AnimatedVisibility(
                    visible = selected,
                    enter = scaleIn(tween(200), initialScale = 0.4f) + fadeIn(tween(200)),
                    exit = scaleOut(tween(150), targetScale = 0.4f) + fadeOut(tween(150))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .background(colors.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = colors.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .clip(itemShape(index, count))
            .background(container)
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

/** A quiet note under the list, with an icon. */
@Composable
private fun FooterNote(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
        )
        Spacer(Modifier.size(MaterialTheme.spacing.s))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
