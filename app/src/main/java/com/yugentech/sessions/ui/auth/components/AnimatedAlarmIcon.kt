package com.yugentech.sessions.ui.auth.components

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.widget.ImageView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.yugentech.sessions.R

@Composable
fun AnimatedAlarmIcon(
    isActivated: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor = MaterialTheme.colorScheme.primary.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.avd_clock_alarm)
                applyColorFilter(iconColor)
            }
        },
        update = { imageView ->
            imageView.applyColorFilter(iconColor)
            if (imageView.isActivated != isActivated) {
                imageView.isActivated = isActivated
                (imageView.drawable as? AnimatedVectorDrawable)?.start()
            }
        }
    )
}

private fun ImageView.applyColorFilter(color: Int) {
    drawable?.colorFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        BlendModeColorFilter(color, BlendMode.SRC_IN)
    } else {
        @Suppress("DEPRECATION")
        PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}
