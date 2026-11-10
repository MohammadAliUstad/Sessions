package com.yugentech.sessions.ui.config.languageScreen

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        val locale = AppLocale.systemLocale(context)
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
                bottom = navBarPadding.calculateBottomPadding() + 8.dp,
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
 * picked one takes the secondary container and a check in a filled circle ([SelectionMark]).
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
        trailingContent = { SelectionMark(selected = selected, ring = ring) },
        modifier = Modifier
            .clip(itemShape(index, count))
            .background(container)
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

/**
 * The radio mark at the end of a row. When a row is picked its circle springs full of the primary
 * colour (a touch past full, then settles), a soft ring ripples out from it, and the check draws
 * itself in as one stroke, short arm first. Unpicking runs it back quickly: the check retracts
 * and the circle drains. Rows that are already picked when the screen opens just show the mark.
 */
@Composable
private fun SelectionMark(selected: Boolean, ring: Color) {
    val colors = MaterialTheme.colorScheme
    val fill = remember { Animatable(if (selected) 1f else 0f) }
    val check = remember { Animatable(if (selected) 1f else 0f) }
    val pulse = remember { Animatable(1f) }
    // What the mark last showed, so the first composition (and a row scrolled back into view)
    // doesn't replay the animation
    val shown = remember { mutableStateOf(selected) }

    LaunchedEffect(selected) {
        if (shown.value == selected) return@LaunchedEffect
        shown.value = selected
        if (selected) {
            launch { fill.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow)) }
            launch {
                pulse.snapTo(0f)
                pulse.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
            }
            delay(90)
            check.animateTo(1f, tween(280, easing = FastOutSlowInEasing))
        } else {
            launch { check.animateTo(0f, tween(120, easing = FastOutLinearInEasing)) }
            launch { pulse.snapTo(1f) }
            fill.animateTo(0f, tween(200, easing = FastOutLinearInEasing))
        }
    }

    val primary = colors.primary
    val onPrimary = colors.onPrimary
    Canvas(modifier = Modifier.size(24.dp)) {
        val radius = size.minDimension / 2f
        val ringWidth = 2.dp.toPx()

        // The ripple: grows past the mark and fades as it goes
        val p = pulse.value
        if (p < 1f) {
            drawCircle(
                color = primary.copy(alpha = 0.35f * (1f - p)),
                radius = radius * (1f + 0.7f * p),
                style = Stroke(width = ringWidth * (1f - 0.5f * p))
            )
        }

        drawCircle(color = ring, radius = radius - ringWidth / 2f, style = Stroke(width = ringWidth))

        val f = fill.value
        if (f > 0f) drawCircle(color = primary, radius = radius * f)

        val c = check.value
        if (c > 0f) {
            val path = Path().apply {
                moveTo(size.width * 0.28f, size.height * 0.52f)
                lineTo(size.width * 0.44f, size.height * 0.67f)
                lineTo(size.width * 0.73f, size.height * 0.36f)
            }
            val measure = PathMeasure().apply { setPath(path, false) }
            val drawn = Path()
            measure.getSegment(0f, measure.length * c, drawn, true)
            drawPath(
                path = drawn,
                color = onPrimary,
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
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
