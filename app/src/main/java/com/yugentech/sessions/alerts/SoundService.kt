package com.yugentech.sessions.alerts

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.yugentech.sessions.R
import timber.log.Timber

class SoundService(
    private val context: Context
) {
    fun playSessionStartSound() {
        Timber.d("Playing session start sound")
        play(R.raw.session_start)
    }

    fun playSessionStopSound() {
        Timber.d("Playing session stop sound")
        play(R.raw.session_end)
    }

    private fun play(@RawRes resId: Int) {
        try {
            // Attempt to create and play the media resource
            MediaPlayer.create(context, resId)?.apply {
                setOnCompletionListener { mediaPlayer ->
                    try {
                        mediaPlayer.release()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to release MediaPlayer after completion")
                    }
                }

                setOnErrorListener { mediaPlayer, what, extra ->
                    // Log the specific media error code for debugging
                    Timber.e("MediaPlayer failed with code: $what, extra: $extra")
                    try {
                        mediaPlayer.release()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to release MediaPlayer during error handling")
                    }
                    true // Return true to indicate error was handled
                }

                start()
            }
        } catch (e: Exception) {
            // Capture initialization failures (e.g., missing resource, codec issues)
            Timber.e(e, "Failed to initialize MediaPlayer for resId: $resId")
        }
    }
}