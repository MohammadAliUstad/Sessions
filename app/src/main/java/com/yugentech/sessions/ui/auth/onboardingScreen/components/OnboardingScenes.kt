package com.yugentech.sessions.ui.auth.onboardingScreen.components

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.theme.builder.getColorScheme
import com.yugentech.sessions.theme.config.ColorTheme
import com.yugentech.sessions.theme.config.ThemeConfiguration
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.ui.auth.components.OnPaper
import com.yugentech.sessions.ui.auth.components.paperColors
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionTimeline
import com.yugentech.sessions.ui.dash.homeScreen.layouts.refined.RefinedTimer
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import com.yugentech.sessions.ui.dash.state.ItemStatus
import com.yugentech.sessions.ui.dash.state.SessionVisualItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// ---------------------------------------------------------------- Shared pieces

/**
 * A 0 → 1 entrance that starts [delayMs] after the scene becomes [active], on a soft spring
 * (it overshoots a touch, so things settle rather than stop).
 */
@Composable
private fun rememberEntrance(active: Boolean, delayMs: Int): Animatable<Float, *> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(active) {
        if (active) {
            delay(delayMs.toLong())
            progress.animateTo(1f, spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow))
        }
    }
    return progress
}

/** Fades in and rises [rise] into place as [progress] runs 0 → 1. */
private fun Modifier.rise(progress: () -> Float, rise: Dp = 24.dp): Modifier = graphicsLayer {
    val t = progress()
    alpha = t.coerceIn(0f, 1f)
    translationY = (1f - t) * rise.toPx()
}

/** A scene's headline and the line under it, rising in after [delayMs]. */
@Composable
private fun SceneText(title: String, body: String, active: Boolean, delayMs: Int) {
    val titleIn = rememberEntrance(active, delayMs)
    val bodyIn = rememberEntrance(active, delayMs + 140)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.rise({ titleIn.value })
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f),
            modifier = Modifier
                .widthIn(max = 420.dp)
                .rise({ bodyIn.value })
        )
    }
}

/** A hint with an icon: plain primary text straight on the aura, no container. */
@Composable
private fun Pill(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = primary)
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = primary)
    }
}

/** Every scene's frame: centred, with room at the sides. */
@Composable
private fun SceneColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { content() }
}

// ---------------------------------------------------------------- 1. Focus

/** The demo's focus block, and how long its 5 minutes take to run here (a quick timelapse). */
private const val DEMO_FOCUS_SECONDS = 5 * 60
private const val DEMO_RUN_MS = 6_000f

/**
 * Home's timer, as the Refined layout draws it: tap it and the wavy ring fills while 5:00 counts
 * down as a timelapse, tap again to pause. When it runs out, rings ripple out from the dial and a
 * check pops in with "Session complete"; it holds a few seconds (or until a tap), then resets.
 */
@Composable
internal fun FocusScene(active: Boolean) {
    val haptic = LocalHapticFeedback.current
    val dialIn = rememberEntrance(active, 150)
    val hintIn = rememberEntrance(active, 1600)
    var running by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Leaving the scene pauses it, as leaving a real session would
    LaunchedEffect(active) { if (!active) running = false }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        var last = withFrameNanos { it }
        while (progress < 1f) {
            val now = withFrameNanos { it }
            progress = (progress + (now - last) / 1_000_000f / DEMO_RUN_MS).coerceAtMost(1f)
            last = now
        }
        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        running = false
    }

    // Run out: rings ripple out from the dial, the end state holds, then it resets for another go
    val full = progress >= 1f
    val ripple = remember { Animatable(0f) }
    LaunchedEffect(full) {
        if (!full) {
            ripple.snapTo(0f)
            return@LaunchedEffect
        }
        launch { ripple.animateTo(1f, tween(1400, easing = FastOutSlowInEasing)) }
        delay(3600)
        progress = 0f
    }
    val accent = modeColor(TimerMode.Focus)

    val remaining = (DEMO_FOCUS_SECONDS * (1f - progress)).toInt()

    SceneColumn {
        // Home's own timer (the Refined layout's), so the demo looks exactly like the app
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .rise({ dialIn.value }, 40.dp)
                // Two rings that spread out from the ring's edge and fade as a block completes
                .drawBehind {
                    val t = ripple.value
                    if (t <= 0f || t >= 1f) return@drawBehind
                    val edge = size.minDimension / 2f - 16.dp.toPx()
                    for (i in 0..1) {
                        val p = ((t - i * 0.18f) / 0.82f).coerceIn(0f, 1f)
                        if (p <= 0f) continue
                        drawCircle(
                            color = accent.copy(alpha = 0.35f * (1f - p)),
                            radius = edge + 44.dp.toPx() * p,
                            style = Stroke(width = 3.dp.toPx() * (1f - p) + 1.dp.toPx())
                        )
                    }
                }
                .pressClickable(shape = CircleShape) {
                    if (progress >= 1f) {
                        // A tap on the end state starts over straight away
                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                        progress = 0f
                        return@pressClickable
                    }
                    haptic.performHapticFeedback(
                        if (running) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn
                    )
                    running = !running
                }
        ) {
            RefinedTimer(
                displayTime = remaining,
                selectedDuration = DEMO_FOCUS_SECONDS,
                isStudying = running,
                currentMode = TimerMode.Focus,
                isSessionActive = running || (progress > 0f && !full),
                idleHint = "Ready when you are",
                showEndTime = false,
                dialSize = 220.dp,
                timeFontSize = 48.sp,
                // A light wash of the ring's own colour, rather than Home's darker surface track
                trackColor = accent.copy(alpha = 0.18f),
                rollingDigits = false,
                animateProgress = false,
                isComplete = full,
                completeTitle = "Session complete",
                completeBody = "5 min of focus"
            )
        }
        Spacer(Modifier.height(20.dp))
        AnimatedContent(
            targetState = full,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(150)) },
            modifier = Modifier.rise({ hintIn.value }, 12.dp),
            label = "focusHint"
        ) { done ->
            if (done) {
                Pill("Nice work! Tap to go again", Icons.Default.TouchApp)
            } else {
                Pill("Tap the dial to start", Icons.Default.TouchApp)
            }
        }
        Spacer(Modifier.height(40.dp))
        SceneText(
            title = "Focus, one block\nat a time",
            body = "Set how long you want to focus, press start, and let Sessions keep the time. Pause whenever you need to.",
            active = active,
            delayMs = 900
        )
    }
}

// ---------------------------------------------------------------- 2. Sets and breaks

/** The demo session: four focus blocks with short breaks between them and a long one before the last. */
private val DemoSchedule = listOf(
    Block.Focus, Block.Short, Block.Focus, Block.Short, Block.Focus, Block.Long, Block.Focus
)
private const val DEMO_SETS = 4

private enum class Block(val runMs: Int) { Focus(1600), Short(800), Long(1100) }

/**
 * A session card like the one on Home: each block's mark fills as it runs, then the next one
 * starts, and Skip sweeps the current block full and moves straight on. It starts over once the
 * last focus block ends.
 */
@Composable
internal fun SessionScene(active: Boolean) {
    val haptic = LocalHapticFeedback.current
    val cardIn = rememberEntrance(active, 200)
    var current by remember { mutableIntStateOf(0) }
    val blockProgress = remember { Animatable(0f) }

    LaunchedEffect(active, current) {
        if (!active) return@LaunchedEffect
        if (current >= DemoSchedule.size) {
            // Everything done: let it sit full for a moment, then go again
            delay(1600)
            current = 0
            return@LaunchedEffect
        }
        blockProgress.snapTo(0f)
        delay(if (current == 0) 900 else 120)
        val block = DemoSchedule[current]
        blockProgress.animateTo(1f, tween(block.runMs, easing = LinearEasing))
        current++
    }

    val items = DemoSchedule.mapIndexed { i, block ->
        val status = when {
            i < current -> ItemStatus.Completed
            i == current -> ItemStatus.Current
            else -> ItemStatus.Upcoming
        }
        when (block) {
            Block.Focus -> SessionVisualItem.FocusBlock(status)
            Block.Short -> SessionVisualItem.ShortBreak(status)
            Block.Long -> SessionVisualItem.LongBreak(status)
        }
    }
    val setsDone = DemoSchedule.take(current.coerceAtMost(DemoSchedule.size)).count { it == Block.Focus }
    val onBreak = current < DemoSchedule.size && DemoSchedule[current] != Block.Focus
    val finished = current >= DemoSchedule.size

    SceneColumn {
        OnPaper {
            val colors = MaterialTheme.colorScheme
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = colors.surfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .rise({ cardIn.value }, 32.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            AnimatedContent(
                                targetState = when {
                                    finished -> "Session complete"
                                    onBreak -> "On a break"
                                    else -> "Focusing"
                                },
                                transitionSpec = { fadeIn(tween(220)).togetherWith(fadeOut(tween(120))) },
                                label = "sessionPhase"
                            ) { phase ->
                                Text(
                                    text = phase,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.Bottom) {
                                AnimatedContent(
                                    targetState = setsDone,
                                    transitionSpec = { fadeIn(tween(220)).togetherWith(fadeOut(tween(120))) },
                                    label = "sessionSets"
                                ) { done ->
                                    Text(
                                        text = "$done",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onSurface
                                    )
                                }
                                Text(
                                    text = "/$DEMO_SETS",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colors.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                                )
                            }
                        }
                        val skipAlpha by animateFloatAsState(if (finished) 0.4f else 1f, tween(250), label = "demoSkip")
                        Surface(
                            shape = CircleShape,
                            color = colors.primary,
                            contentColor = colors.onPrimary,
                            modifier = Modifier
                                .graphicsLayer { alpha = skipAlpha }
                                .pressClickable(shape = CircleShape, enabled = !finished) {
                                    haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                    current++
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Skip", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    SessionTimeline(
                        items = items,
                        progress = blockProgress.value,
                        ink = colors.onSurface
                    )
                }
            }
        }
        Spacer(Modifier.height(44.dp))
        SceneText(
            title = "Sets, with breaks\nbuilt in",
            body = "Stack focus blocks into a session. Short breaks fall between them, a long one comes before the last, and Skip moves you on when you're ready.",
            active = active,
            delayMs = 700
        )
    }
}

// ---------------------------------------------------------------- 3. Ambience

private class Sound(val name: String, val icon: ImageVector)

private val Sounds = listOf(
    Sound("Rain", Icons.Default.WaterDrop),
    Sound("Brown noise", Icons.Default.GraphicEq),
    Sound("Fireplace", Icons.Default.LocalFireDepartment),
    Sound("Library", Icons.Default.LocalLibrary),
    Sound("Riverside", Icons.Default.Waves),
    Sound("Forest", Icons.Default.Forest)
)

/**
 * The six soundscapes as plain tiles under the scene's text: nothing plays and nothing moves
 * beyond the tiles rising in with the rest of the scene.
 */
@Composable
internal fun AmbienceScene(active: Boolean) {
    SceneColumn {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Sounds.chunked(3).forEachIndexed { row, sounds ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    sounds.forEachIndexed { column, sound ->
                        val index = row * 3 + column
                        SoundTile(
                            sound = sound,
                            entrance = rememberEntrance(active, 200 + index * 90),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(40.dp))
        SceneText(
            title = "Set the mood",
            body = "Rain on the window, a crackling fire, a quiet library. Pick a soundscape to play under your sessions, or keep it silent.",
            active = active,
            delayMs = 800
        )
    }
}

/** One soundscape: a glassy wash of the text colour over the aura, with a hairline edge. */
@Composable
private fun SoundTile(
    sound: Sound,
    entrance: Animatable<Float, *>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .height(92.dp)
            .rise({ entrance.value }, 20.dp)
            .clip(shape)
            .background(colors.onBackground.copy(alpha = 0.06f))
            .border(1.dp, colors.onBackground.copy(alpha = 0.10f), shape)
            .padding(8.dp)
    ) {
        Icon(
            sound.icon,
            contentDescription = null,
            tint = colors.onBackground.copy(alpha = 0.85f),
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = sound.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground.copy(alpha = 0.85f),
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------- 4. Make it yours

private enum class LookMode(val label: String, val icon: ImageVector) {
    Light("Light", Icons.Default.LightMode),
    Dark("Dark", Icons.Default.DarkMode),
    Amoled("AMOLED", Icons.Default.Contrast)
}

private class PaletteOption(val theme: ColorTheme, val name: String)

/**
 * The palettes in Appearance's order. Dynamic (the wallpaper's colours) only joins on Android 12
 * and up, where the app can offer it.
 */
private val PaletteOptions: List<PaletteOption> = buildList {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(PaletteOption(ColorTheme.DYNAMIC, "Dynamic"))
    add(PaletteOption(ColorTheme.SESSIONS, "Sessions"))
    add(PaletteOption(ColorTheme.LAGOON, "Lagoon"))
    add(PaletteOption(ColorTheme.GROVE, "Grove"))
    add(PaletteOption(ColorTheme.HARVEST, "Harvest"))
    add(PaletteOption(ColorTheme.SAKURA, "Sakura"))
    add(PaletteOption(ColorTheme.ALPINE, "Alpine"))
    add(PaletteOption(ColorTheme.TWILIGHT, "Twilight"))
}

private enum class Facets { Palette, Mode }

/**
 * A little phone showing the app, built the way Appearance builds it (the same colour schemes,
 * deepened dark surfaces and AMOLED black). The two tiles under it are buttons: each tap steps
 * that part of the look on (the next palette, or Light → Dark → AMOLED) and the
 * phone follows along, with the tile just tapped lit in the accent colour.
 */
@Composable
internal fun ThemeScene(active: Boolean) {
    val haptic = LocalHapticFeedback.current
    val phoneIn = rememberEntrance(active, 150)
    val facetsIn = rememberEntrance(active, 450)
    val hintIn = rememberEntrance(active, 1400)
    var paletteIndex by remember { mutableIntStateOf(0) }
    var mode by remember { mutableStateOf(LookMode.Light) }
    var lastTapped by remember { mutableStateOf<Facets?>(null) }

    val palette = PaletteOptions[paletteIndex]
    val scheme = getColorScheme(
        themeConfiguration = ThemeConfiguration(
            themeMode = if (mode == LookMode.Light) ThemeMode.LIGHT else ThemeMode.DARK,
            colorTheme = palette.theme,
            isAmoledMode = mode == LookMode.Amoled
        )
    )

    fun tap(facet: Facets) {
        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
        lastTapped = facet
        when (facet) {
            Facets.Palette -> paletteIndex = (paletteIndex + 1) % PaletteOptions.size
            Facets.Mode -> mode = LookMode.entries[(mode.ordinal + 1) % LookMode.entries.size]
        }
    }

    SceneColumn {
        MiniApp(
            scheme = scheme,
            modifier = Modifier.rise({ phoneIn.value }, 36.dp)
        )
        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .rise({ facetsIn.value }, 16.dp)
        ) {
            Facet(
                icon = Icons.Default.Palette,
                value = palette.name,
                lit = lastTapped == Facets.Palette,
                onClick = { tap(Facets.Palette) },
                modifier = Modifier.weight(1f)
            )
            Facet(
                icon = mode.icon,
                value = mode.label,
                lit = lastTapped == Facets.Mode,
                onClick = { tap(Facets.Mode) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(4.dp))
        Pill(
            text = "Tap a tile to try it",
            icon = Icons.Default.TouchApp,
            modifier = Modifier.rise({ hintIn.value }, 12.dp)
        )
        Spacer(Modifier.height(24.dp))
        SceneText(
            title = "Make it yours",
            body = "Pick a palette or your wallpaper's colours, and go light, dark or true-black AMOLED. Every screen follows along.",
            active = active,
            delayMs = 800
        )
    }
}

/**
 * A pocket version of the app in [scheme]: a greeting, the timer ring with the time, a Start
 * button and the bottom bar. Colours glide to each new look.
 */
@Composable
private fun MiniApp(scheme: ColorScheme, modifier: Modifier = Modifier) {
    val spec = tween<Color>(500)
    val background by animateColorAsState(scheme.background, spec, label = "miniBackground")
    val container by animateColorAsState(scheme.surfaceContainer, spec, label = "miniContainer")
    val track by animateColorAsState(scheme.surfaceContainerHighest, spec, label = "miniTrack")
    val primary by animateColorAsState(scheme.primary, spec, label = "miniPrimary")
    val onPrimary by animateColorAsState(scheme.onPrimary, spec, label = "miniOnPrimary")
    val ink by animateColorAsState(scheme.onSurface, spec, label = "miniInk")
    val softInk by animateColorAsState(scheme.onSurfaceVariant, spec, label = "miniSoftInk")
    val selected by animateColorAsState(scheme.secondaryContainer, spec, label = "miniSelected")
    val onSelected by animateColorAsState(scheme.onSecondaryContainer, spec, label = "miniOnSelected")

    val typography = MaterialTheme.typography
    // A dark bezel, as a phone has, so the screen's edge reads in every look (AMOLED black too)
    Box(
        modifier = modifier
            .size(width = 152.dp, height = 296.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF16161A))
            .padding(5.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                MiniText("Good evening", typography.titleSmall, FontWeight.SemiBold, ink)
                MiniText("Ready to focus?", typography.labelSmall, FontWeight.Normal, softInk)
            }
            Spacer(Modifier.weight(1f))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(104.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 7.dp.toPx()
                    val topLeft = Offset(stroke / 2f, stroke / 2f)
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    drawArc(
                        color = track, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                        topLeft = topLeft, size = arcSize, style = Stroke(stroke)
                    )
                    drawArc(
                        color = primary, startAngle = -90f, sweepAngle = 250f, useCenter = false,
                        topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                MiniText("25:00", typography.headlineSmall, FontWeight.Bold, ink)
            }
            Spacer(Modifier.height(14.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(primary)
                    .padding(horizontal = 20.dp, vertical = 7.dp)
            ) {
                MiniText("Start", typography.labelMedium, FontWeight.SemiBold, onPrimary)
            }
            Spacer(Modifier.weight(1f))
            // The bottom bar: home picked, two more tabs beside it
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(container)
                    .padding(vertical = 10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 32.dp, height = 18.dp)
                        .clip(CircleShape)
                        .background(selected)
                ) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(onSelected)
                    )
                }
                repeat(2) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(softInk.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

/** One line of the mini app's text. */
@Composable
private fun MiniText(text: String, style: TextStyle, weight: FontWeight, color: Color) {
    Text(text = text, style = style, fontWeight = weight, color = color, maxLines = 1)
}

/**
 * A button for one part of the look (palette or mode), glassy like the sound tiles: a faint wash
 * with a hairline edge, which takes on the accent colour when it was just tapped.
 */
@Composable
private fun Facet(
    icon: ImageVector,
    value: String,
    lit: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(
        if (lit) colors.primary.copy(alpha = 0.16f) else colors.onBackground.copy(alpha = 0.06f),
        tween(400),
        label = "facet"
    )
    val content by animateColorAsState(
        if (lit) colors.primary else colors.onBackground.copy(alpha = 0.85f),
        tween(400),
        label = "facetContent"
    )
    val outline by animateColorAsState(
        if (lit) colors.primary.copy(alpha = 0.35f) else colors.onBackground.copy(alpha = 0.10f),
        tween(400),
        label = "facetOutline"
    )
    val shape = RoundedCornerShape(18.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .pressClickable(shape = shape, onClick = onClick)
            .clip(shape)
            .background(container)
            .border(1.dp, outline, shape)
            .padding(vertical = 10.dp, horizontal = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        AnimatedContent(
            targetState = value,
            transitionSpec = { fadeIn(tween(220)).togetherWith(fadeOut(tween(120))) },
            label = "facetValue"
        ) { shown ->
            Text(
                text = shown,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = content,
                maxLines = 1
            )
        }
    }
}

// ---------------------------------------------------------------- 5. Watch it grow

private const val HEAT_WEEKS = 12
private const val DEMO_HOURS = 48

/**
 * A focus heatmap filling in week by week, deeper for longer days, then the hours focused
 * counting up underneath.
 */
@Composable
internal fun GrowthScene(active: Boolean) {
    // The card is in the app's own colours: its heatmap and total use the accent colour
    val colors = paperColors()
    val haptic = LocalHapticFeedback.current
    val cardIn = rememberEntrance(active, 300)
    val fill = remember { Animatable(0f) }
    var hours by remember { mutableIntStateOf(0) }
    // How long was focused each day: mostly something, more toward the recent weeks.
    val days = remember {
        val random = Random(7)
        List(HEAT_WEEKS * 7) { i ->
            val recent = i / (HEAT_WEEKS * 7f)
            if (random.nextFloat() < 0.3f - recent * 0.22f) 0f else (0.2f + random.nextFloat() * 0.5f + recent * 0.3f).coerceAtMost(1f)
        }
    }
    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        delay(600)
        fill.animateTo(HEAT_WEEKS.toFloat(), tween(1500, easing = FastOutSlowInEasing))
        for (h in 1..DEMO_HOURS) {
            hours = h
            if (h % 8 == 0) haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
            delay(22)
        }
    }

    SceneColumn {
        SceneText(
            title = "Watch it grow",
            body = "Every focused minute counts. Insights, a year-long heatmap and gentle daily reminders keep you coming back.",
            active = active,
            delayMs = 100
        )
        Spacer(Modifier.height(32.dp))
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colors.surfaceContainer,
            modifier = Modifier
                .fillMaxWidth()
                .rise({ cardIn.value }, 28.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    repeat(HEAT_WEEKS) { week ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(7) { day ->
                                val amount = days[week * 7 + day]
                                // Each week's column lights up as the fill sweeps past it
                                val lit = (fill.value - week).coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.onSurfaceVariant.copy(alpha = 0.10f))
                                        .graphicsLayer { alpha = lit }
                                        .background(colors.primary.copy(alpha = amount))
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${hours}h",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = colors.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "focused this season",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
        }
    }
}
