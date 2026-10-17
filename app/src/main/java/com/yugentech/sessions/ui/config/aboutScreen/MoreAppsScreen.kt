package com.yugentech.sessions.ui.config.aboutScreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.yugentech.sessions.ui.config.aboutScreen.components.AnimatedQuillIcon
import com.yugentech.sessions.ui.config.aboutScreen.components.AnimatedRyoriIcon
import com.yugentech.sessions.ui.config.aboutScreen.components.AnimatedHajimeIcon
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreAppsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("More from us") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { IntroText() }

            // --- Quill ---
            item {
                QuillHeroSection(
                    onDownloadClick = { openPlayStore(context, "com.yugentech.quill") }
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionLabel("Key Features")
                    QuillCapabilitiesCarousel()
                }
            }

            item { SectionDivider() }

            // --- Hajime ---
            item { HajimeHeroSection() }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionLabel("Key Features")
                    HajimeCapabilitiesCarousel()
                }
            }

            item { SectionDivider() }

            // --- Ryori ---
            item { RyoriHeroSection() }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionLabel("Key Features")
                    RyoriCapabilitiesCarousel()
                }
            }

            item { SectionDivider() }

            item { ClosingCard() }
        }
    }
}

@Composable
private fun IntroText() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Three more apps, made with the same care",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "If you enjoy focusing with Sessions, you might like reading with Quill, learning Japanese with Hajime and cooking with Ryori.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// A quiet break between sections: two hairlines with a small star in the middle.
@Composable
private fun SectionDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = "✦",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

// ============================== Quill ==============================

@Composable
private fun QuillHeroSection(onDownloadClick: () -> Unit) {
    AppHeroSection(
        name = "Quill",
        tagline = "Read Deeper. Think Further.",
        iconBackground = QuillIconBackground,
        animationMillis = 1900,
        onDownloadClick = onDownloadClick
    ) { isAnimating ->
        AnimatedQuillIcon(
            isAnimating = isAnimating,
            modifier = Modifier.requiredSize(64.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuillCapabilitiesCarousel() {
    CapabilitiesCarousel(
        items = listOf(
            CapabilityItem(
                icon = Icons.Outlined.Psychology,
                title = "Aira",
                description = "An AI reading companion that answers from your book, locked to your progress so it never spoils what's next.",
                slot = 0
            ),
            CapabilityItem(
                icon = Icons.Outlined.AutoAwesome,
                title = "Character Companion",
                description = "Recall who someone is, what they're up to lately, or trace their whole journey so far.",
                slot = 1
            ),
            CapabilityItem(
                icon = Icons.Outlined.Image,
                title = "Visualize Scenes",
                description = "Turn a passage into an illustration, then jump straight back to where it came from.",
                slot = 2
            ),
            CapabilityItem(
                icon = Icons.Outlined.AutoStories,
                title = "A Beautiful Reader",
                description = "Fonts, spacing, page themes, highlights, night light and paged or scrolling layouts.",
                slot = 0
            ),
            CapabilityItem(
                icon = Icons.Outlined.LocalLibrary,
                title = "Endless Library",
                description = "Import your own EPUBs or download from 60,000+ free Project Gutenberg and Standard Ebooks titles.",
                slot = 1
            ),
            CapabilityItem(
                icon = Icons.Outlined.Insights,
                title = "Reading Insights",
                description = "Streaks, a reading heatmap, peak hours and favorite authors, with gentle daily reminders.",
                slot = 2
            )
        )
    )
}

// ============================== Ryori ==============================

@Composable
private fun RyoriHeroSection() {
    AppHeroSection(
        name = "Ryori",
        tagline = "Cook Something Wonderful",
        iconBackground = RyoriIconBackground,
        animationMillis = 1200,
        onDownloadClick = null
    ) { isAnimating ->
        // Fills the tile: the drawable has a margin round the burger (still 64dp), so the buns
        // slide in from the tile's edges.
        AnimatedRyoriIcon(
            isAnimating = isAnimating,
            modifier = Modifier.requiredSize(88.dp)
        )
    }
}

@Composable
private fun RyoriCapabilitiesCarousel() {
    CapabilitiesCarousel(
        // Third carousel on the screen: secondary, tertiary, primary.
        colorOffset = 1,
        items = listOf(
            CapabilityItem(
                icon = Icons.Outlined.Home,
                title = "A Home That Inspires",
                description = "A featured carousel, a cuisine and a category of the day, and a Surprise me button for when you can't decide.",
                slot = 0
            ),
            CapabilityItem(
                icon = Icons.Outlined.TravelExplore,
                title = "Explore Every Kitchen",
                description = "Browse recipes by category, cuisine or ingredient, or search for one by name.",
                slot = 1
            ),
            CapabilityItem(
                icon = Icons.Outlined.Checklist,
                title = "Cook Along",
                description = "Clear step-by-step instructions, with ingredients you can tick off as you go.",
                slot = 2
            ),
            CapabilityItem(
                icon = Icons.Outlined.LocalBar,
                title = "Mocktails",
                description = "Non-alcoholic drinks sit right next to the meals, so there's something for every glass.",
                slot = 0
            ),
            CapabilityItem(
                icon = Icons.Outlined.Eco,
                title = "Vegetarian Mode",
                description = "One switch hides meat and seafood across the whole app.",
                slot = 1
            ),
            CapabilityItem(
                icon = Icons.Outlined.CloudOff,
                title = "Works Offline",
                description = "Recipes you've opened are saved on your device and load instantly, even without a connection.",
                slot = 2
            )
        )
    )
}

// ============================== Hajime ==============================

@Composable
private fun HajimeHeroSection() {
    AppHeroSection(
        name = "Hajime",
        tagline = "Learn to Read Japanese",
        iconBackground = HajimeIconBackground,
        animationMillis = 1660,
        onDownloadClick = null
    ) { isAnimating ->
        AnimatedHajimeIcon(
            isAnimating = isAnimating,
            modifier = Modifier.requiredSize(156.dp)
        )
    }
}

@Composable
private fun HajimeCapabilitiesCarousel() {
    CapabilitiesCarousel(
        // Second carousel on the screen: tertiary, primary, secondary.
        colorOffset = 2,
        items = listOf(
            CapabilityItem(
                icon = Icons.Outlined.Translate,
                title = "Learn to Read Japanese",
                description = "Hiragana and katakana first, then kanji, words and sentences, one short lesson at a time.",
                slot = 0
            ),
            CapabilityItem(
                icon = Icons.Outlined.School,
                title = "Every JLPT Kanji",
                description = "N5 to N1 kanji with readings, meanings, parts, common words and example sentences with furigana.",
                slot = 1
            ),
            CapabilityItem(
                icon = Icons.Outlined.History,
                title = "Reviews That Remember",
                description = "Spaced repetition brings each item back just before you'd forget it, so what you learn sticks.",
                slot = 2
            ),
            CapabilityItem(
                icon = Icons.Outlined.AutoAwesome,
                title = "Sensei, Your AI Tutor",
                description = "Ask anything about Japanese and get stories, sentence breakdowns and quizzes built from what you've learned.",
                slot = 0
            ),
            CapabilityItem(
                icon = Icons.Outlined.RecordVoiceOver,
                title = "Hear Every Word",
                description = "Every kana, kanji, word and sentence can be read aloud in a Japanese voice.",
                slot = 1
            ),
            CapabilityItem(
                icon = Icons.Outlined.Insights,
                title = "Study Insights",
                description = "Streaks, a study heatmap, your trickiest mix-ups and progress through every JLPT level.",
                slot = 2
            )
        )
    )
}

// ============================== Shared ==============================

// App tile with the icon on its launcher background colour; tap to replay the animation. With no
// onDownloadClick the app isn't out yet, so it shows Coming soon instead of the button.
@Composable
private fun AppHeroSection(
    name: String,
    tagline: String,
    iconBackground: Color,
    animationMillis: Int,
    onDownloadClick: (() -> Unit)?,
    icon: @Composable (isAnimating: Boolean) -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(iconBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                delay(animationMillis.milliseconds)
                                isAnimating = false
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                icon(isAnimating)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onDownloadClick == null) {
                    ComingSoonLabel()
                } else {
                    Button(
                        onClick = onDownloadClick,
                        modifier = Modifier.height(40.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Get on Play Store",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

// Stands in for the Play Store button until an app is out.
@Composable
private fun ComingSoonLabel() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = "Coming soon",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// colorOffset shifts where the primary / secondary / tertiary cycle starts, so two carousels on
// the same screen don't repeat the same colour order.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CapabilitiesCarousel(items: List<CapabilityItem>, colorOffset: Int = 0) {
    val containerColors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    val contentColors = listOf(
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.onTertiaryContainer
    )

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.size },
        preferredItemWidth = 256.dp,
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier.fillMaxWidth()
    ) { index ->
        val item = items[index]
        val colorIndex = (item.slot + colorOffset) % containerColors.size
        val bg = containerColors[colorIndex]
        val fg = contentColors[colorIndex]

        Card(
            modifier = Modifier
                .height(196.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            colors = CardDefaults.cardColors(containerColor = bg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(30.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = fg
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = fg.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// One card for the whole family of apps, same as Ryori's More from us screen.
@Composable
private fun ClosingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // One simple icon per app, in the same order as the headline below.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OverviewIcon(icon = Icons.Outlined.Timer, contentDescription = "Sessions")
                OverviewIcon(icon = Icons.Outlined.AutoStories, contentDescription = "Quill")
                OverviewIcon(icon = Icons.Outlined.Translate, contentDescription = "Hajime")
                OverviewIcon(icon = Icons.Outlined.LunchDining, contentDescription = "Ryori")
            }
            Text(
                text = "Focus. Read. Learn. Cook.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Sessions, Quill, Hajime and Ryori are made by YugenTech: small, thoughtful apps built to make everyday moments a little calmer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun OverviewIcon(icon: ImageVector, contentDescription: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(26.dp)
        )
    }
}

// ============================== Sheet ==============================

// One app in the sheet. The icon is drawn in a 56dp tile, so each is scaled from its 88dp one.
// A null packageName means the app isn't out yet.
private class SheetEntry(
    val name: String,
    val tagline: String,
    val packageName: String?,
    val iconBackground: Color,
    val animationMillis: Long,
    val icon: @Composable (isAnimating: Boolean) -> Unit
)

/**
 * The short version of this screen, opened from Settings' About section: each app's icon, name
 * and tagline with a Play Store button, then a link to the full screen. Tapping an icon
 * plays its animation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreFromUsSheet(onDismiss: () -> Unit, onSeeAll: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val apps = listOf(
        SheetEntry("Quill", "Read Deeper. Think Further.", "com.yugentech.quill", QuillIconBackground, 1900) {
            AnimatedQuillIcon(isAnimating = it, modifier = Modifier.requiredSize(41.dp))
        },
        SheetEntry("Hajime", "Learn to Read Japanese", null, HajimeIconBackground, 1660) {
            AnimatedHajimeIcon(isAnimating = it, modifier = Modifier.requiredSize(100.dp))
        },
        SheetEntry("Ryori", "Cook Something Wonderful", null, RyoriIconBackground, 1200) {
            AnimatedRyoriIcon(isAnimating = it, modifier = Modifier.requiredSize(56.dp))
        }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        // Sessions' sheet handle: a small pill with roomy padding, so it's easy to grab.
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .padding(vertical = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 6.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "More from us",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Three more apps, made with the same care.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Column(
                modifier = Modifier.padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                apps.forEachIndexed { index, app ->
                    SheetAppRow(
                        app = app,
                        index = index,
                        count = apps.size,
                        onGetApp = app.packageName?.let { pkg -> { openPlayStore(context, pkg) } }
                    )
                }
            }

            TextButton(
                onClick = {
                    // Slides the sheet away first, so it doesn't vanish under the new screen.
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                        onSeeAll()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp)
            ) {
                Text("See all features")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SheetAppRow(app: SheetEntry, index: Int, count: Int, onGetApp: (() -> Unit)?) {
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val play: () -> Unit = {
        if (!isAnimating) {
            isAnimating = true
            scope.launch {
                delay(app.animationMillis)
                isAnimating = false
            }
        }
    }
    val large = 20.dp
    val small = 6.dp
    Surface(
        shape = RoundedCornerShape(
            topStart = if (index == 0) large else small,
            topEnd = if (index == 0) large else small,
            bottomStart = if (index == count - 1) large else small,
            bottomEnd = if (index == count - 1) large else small
        ),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(app.iconBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = play
                    )
            ) {
                app.icon(isAnimating)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = app.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onGetApp == null) {
                ComingSoonLabel()
            } else {
                FilledTonalButton(
                    onClick = onGetApp,
                    contentPadding = PaddingValues(horizontal = 18.dp)
                ) {
                    Text("Get")
                }
            }
        }
    }
}

// Play Store app if installed, otherwise the store's web page.
private fun openPlayStore(context: Context, packageName: String) {
    val market = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
    val web = Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri())
    runCatching { context.startActivity(market) }
        .onFailure { runCatching { context.startActivity(web) } }
}

// Launcher background colours, so each icon sits on the same tile as on the home screen.
private val QuillIconBackground = Color(0xFF576421)
private val RyoriIconBackground = Color(0xFFFFFBEB)
private val HajimeIconBackground = Color(0xFFF5F0E8)

private data class CapabilityItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val slot: Int
)
