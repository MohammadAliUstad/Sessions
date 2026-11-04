package com.yugentech.sessions.ui.config.aboutScreen.components

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yugentech.sessions.R

// Same as AnimatedRyoriIcon, for Mynt's wallet ("Payday"). It rests on the finished icon and replays
// its pop, bills and clasp flip when isAnimating becomes true (the AVD runs for 1260ms).
@Composable
fun AnimatedMyntIcon(
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.avd_mynt)
            }
        },
        update = { imageView ->
            if (isAnimating) {
                (imageView.drawable as? Animatable)?.let {
                    it.stop()
                    it.start()
                }
            }
        }
    )
}
