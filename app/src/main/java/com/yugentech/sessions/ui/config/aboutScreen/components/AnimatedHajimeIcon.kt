package com.yugentech.sessions.ui.config.aboutScreen.components

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yugentech.sessions.R

// Same as AnimatedRyoriIcon, for Hajime's 初 mark. It rests on the finished character and replays
// its stroke-by-stroke writing when isAnimating becomes true (the AVD runs for 1660ms).
@Composable
fun AnimatedHajimeIcon(
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.avd_hajime)
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
