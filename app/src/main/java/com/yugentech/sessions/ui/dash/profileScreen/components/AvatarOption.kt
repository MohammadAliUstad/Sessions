package com.yugentech.sessions.ui.dash.profileScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.spacing
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// Size of each avatar in the avatar sheet's grid, three to a row.
internal val AvatarOptionSize = 112.dp

// How far the selected avatar's shape turns as it blooms, in degrees.
private const val SelectedSpin = 40f

// The avatar name's font weight, unselected and selected; it thickens smoothly between the two.
private const val LabelWeightRest = 400
private const val LabelWeightSelected = 600

// Round Material shapes that fill the circle about as fully as the cookie does, so the figure
// cropped to any of them keeps its face. All symmetric, so the turn never leaves one lopsided.
private val SelectedShapes = listOf(
    MaterialShapes.Cookie9Sided,
    MaterialShapes.Cookie7Sided,
    MaterialShapes.Cookie12Sided,
    MaterialShapes.Clover8Leaf,
    MaterialShapes.SoftBurst,
    MaterialShapes.Sunny
)

// Unselected avatars sit on a plain circle. Selecting one blooms its circle into one of
// [SelectedShapes], picked at random each time (never the same one twice in a row), in
// primaryContainer with a small turn; the figure stays upright and is cropped to the same
// shape, so it sits inside it rather than on top of it.
@Composable
fun AvatarOption(
    avatar: Avatar,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    // The last shape picked, kept so a deselected avatar eases back from the shape it showed
    val lastShape = remember { intArrayOf(Random.nextInt(SelectedShapes.size)) }
    val shapeIndex = remember(isSelected) {
        if (isSelected) {
            val next = Random.nextInt(SelectedShapes.size - 1)
            (if (next >= lastShape[0]) next + 1 else next).also { lastShape[0] = it }
        } else {
            lastShape[0]
        }
    }
    val morph = remember(shapeIndex) { Morph(MaterialShapes.Circle, SelectedShapes[shapeIndex]) }

    // Settles without overshooting: the shape eases in and the name thickens with it
    val progress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "avatarShapeMorph"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) SelectedSpin else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "avatarShapeSpin"
    )
    val backdrop by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        label = "avatarShapeColor"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "avatarLabelColor"
    )
    val shape = MorphShape(morph, progress, rotation)

    Column(
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
    ) {
        Box(
            modifier = Modifier
                .size(AvatarOptionSize)
                .clip(shape)
                .background(backdrop)
                .clickable(onClick = onSelect),
            contentAlignment = Alignment.Center
        ) {
            AvatarImage(
                avatarId = avatar.id,
                size = AvatarOptionSize,
                imageSize = 134.dp,
                imageOffsetY = 6.dp
            )
        }

        // Rides the shape's spring, so the name thickens in step with it. Rounded to steps of 10
        // so only a handful of weights are ever built (and each is cached once it has been).
        val labelWeight = (LabelWeightRest + (LabelWeightSelected - LabelWeightRest) * progress)
            .roundToInt()
            .let { (it + 5) / 10 * 10 }
        val baseFamily = MaterialTheme.typography.labelMedium.fontFamily
        val labelFamily = remember(baseFamily, labelWeight) { fontFamilyAtWeight(baseFamily, labelWeight) }

        Text(
            text = stringResource(avatar.name),
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
            fontFamily = labelFamily,
            fontWeight = FontWeight(labelWeight),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee(
                animationMode = MarqueeAnimationMode.Immediately,
                repeatDelayMillis = AppAnimations.Durations.RepeatDelay,
                initialDelayMillis = AppAnimations.Durations.Delay,
                velocity = AppAnimations.Motion.Velocity
            )
        )
    }
}

// The theme's fonts are variable, but its family only lists five fixed weights, so a weight in
// between would snap to the nearest. This builds the same font file at exactly [weight] instead,
// so the weight can move smoothly. Falls back to [family] as it is if it isn't a resource font.
@OptIn(ExperimentalTextApi::class)
private fun fontFamilyAtWeight(family: FontFamily?, weight: Int): FontFamily? {
    val font = (family as? FontListFontFamily)?.fonts?.firstOrNull() as? ResourceFont ?: return family
    return FontFamily(
        Font(
            resId = font.resId,
            weight = FontWeight(weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight))
        )
    )
}

// The [morph] at [progress], turned [rotation] degrees about its centre and stretched over the
// bounds. MaterialShapes are drawn in a 0..1 square, so each point is scaled up to the size.
private class MorphShape(
    private val morph: Morph,
    private val progress: Float,
    private val rotation: Float
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val radians = Math.toRadians(rotation.toDouble())
        val c = cos(radians).toFloat()
        val s = sin(radians).toFloat()
        fun x(px: Float, py: Float) = ((px - 0.5f) * c - (py - 0.5f) * s + 0.5f) * size.width
        fun y(px: Float, py: Float) = ((px - 0.5f) * s + (py - 0.5f) * c + 0.5f) * size.height

        val path = Path()
        morph.asCubics(progress).forEachIndexed { i, cubic ->
            if (i == 0) path.moveTo(x(cubic.anchor0X, cubic.anchor0Y), y(cubic.anchor0X, cubic.anchor0Y))
            path.cubicTo(
                x(cubic.control0X, cubic.control0Y), y(cubic.control0X, cubic.control0Y),
                x(cubic.control1X, cubic.control1Y), y(cubic.control1X, cubic.control1Y),
                x(cubic.anchor1X, cubic.anchor1Y), y(cubic.anchor1X, cubic.anchor1Y)
            )
        }
        path.close()
        return Outline.Generic(path)
    }
}
