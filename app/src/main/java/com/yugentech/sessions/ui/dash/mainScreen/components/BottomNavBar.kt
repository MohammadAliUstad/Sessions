package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.yugentech.sessions.R
import com.yugentech.sessions.navigation.screen.BottomBarScreen
import kotlinx.coroutines.delay

/** Height of the dock. */
private val DockHeight: Dp = 64.dp

/** Height of each tab's capsule inside the dock. */
private val TabHeight = 48.dp

/** Space between the dock and the system navigation bar. */
private val DockBottomGap = 8.dp

/**
 * A floating pill dock, as on Hajime: each tab is just its animated icon, and the selected one
 * grows into a filled capsule that also spells out its name.
 */
@Composable
fun BottomNavBar(
    items: List<BottomBarScreen>,
    currentScreen: BottomBarScreen,
    onSelected: (BottomBarScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = DockBottomGap),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.height(DockHeight),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items.forEach { screen ->
                    DockTab(
                        screen = screen,
                        isSelected = screen == currentScreen,
                        onClick = { if (screen != currentScreen) onSelected(screen) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DockTab(
    screen: BottomBarScreen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val motion = MaterialTheme.motionScheme
    val container by animateColorAsState(
        targetValue = if (isSelected) colors.primaryContainer else Color.Transparent,
        animationSpec = motion.fastEffectsSpec(),
        label = "dockTabContainer"
    )
    val content by animateColorAsState(
        targetValue = if (isSelected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        animationSpec = motion.fastEffectsSpec(),
        label = "dockTabContent"
    )
    val horizontalPad by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 12.dp,
        animationSpec = motion.fastSpatialSpec(),
        label = "dockTabPad"
    )
    val animationRes = when (screen) {
        BottomBarScreen.Profile -> R.raw.account
        BottomBarScreen.Home -> R.raw.clock
        BottomBarScreen.Settings -> R.raw.settings
    }

    val title = stringResource(screen.title)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(TabHeight)
            .clip(CircleShape)
            .background(container)
            // No ripple and no press scale: the capsule growing is the feedback
            .selectable(
                selected = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { contentDescription = title }
            .padding(horizontal = horizontalPad)
    ) {
        CompositionLocalProvider(LocalContentColor provides content) {
            AnimatedNavIcon(jsonResId = animationRes, shouldPlay = isSelected)
        }
        // The name slides out beside the icon on the selected tab only
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(motion.fastEffectsSpec()) +
                expandHorizontally(motion.fastSpatialSpec<IntSize>(), expandFrom = Alignment.Start),
            exit = fadeOut(motion.fastEffectsSpec()) +
                shrinkHorizontally(motion.fastSpatialSpec<IntSize>(), shrinkTowards = Alignment.Start)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = content,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun AnimatedNavIcon(
    jsonResId: Int,
    shouldPlay: Boolean,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(shouldPlay) {
        if (shouldPlay) isAnimating = true
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(jsonResId))
    val currentColor = LocalContentColor.current.toArgb()

    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = currentColor,
            keyPath = arrayOf("**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = currentColor,
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        composition = composition,
        modifier = modifier.size(24.dp),
        isPlaying = isAnimating,
        iterations = 1,
        dynamicProperties = dynamicProperties,
        maintainOriginalImageBounds = true
    )

    // Let the icon's animation finish even if the tab is deselected mid-play, then reset it
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            composition?.let {
                delay(it.duration.toLong())
                isAnimating = false
            }
        }
    }
}
