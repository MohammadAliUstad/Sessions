// AnimatedSessionsIcon.kt
package com.yugentech.sessions.ui.config.aboutScreen.components

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yugentech.sessions.R

@Composable
fun AnimatedSessionsIcon(
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.ic_sessions_animated)
            }
        },
        update = { imageView ->
            if (isAnimating) {
                (imageView.drawable as? Animatable)?.start()
            }
        }
    )
}