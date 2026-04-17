package com.yugentech.quill.ui.access.signIn.components

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.widget.ImageView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.yugentech.quill.R

@Composable
fun AnimatedEyeIcon(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.asl_trimclip_eye)

                // Now we can just use the native isActivated property!
                this.isActivated = isVisible

                applyColorFilter(iconColor)
            }
        },
        update = { imageView ->
            imageView.drawable?.colorFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                BlendModeColorFilter(iconColor, BlendMode.SRC_IN)
            } else {
                @Suppress("DEPRECATION")
                PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
            }

            if (imageView.isActivated != isVisible) {
                imageView.isActivated = isVisible

                imageView.drawable?.colorFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    BlendModeColorFilter(iconColor, BlendMode.SRC_IN)
                } else {
                    @Suppress("DEPRECATION")
                    PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
                }
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