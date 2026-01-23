package com.yugentech.sessions.alerts

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.yugentech.sessions.R
import timber.log.Timber

class SoundService(
    private val context: Context
) {
    fun playStartAlert() {
        Timber.d("Playing session start sound")
        play(R.raw.session_start)
    }

    fun playStopAlert() {
        Timber.d("Playing session stop sound")
        play(R.raw.session_stop, 0.5f)
    }

    private fun play(@RawRes resId: Int, volume: Float = 0.6f) {
        try {
            MediaPlayer.create(context, resId)?.apply {
                setVolume(volume, volume)

                setOnCompletionListener { mediaPlayer ->
                    try {
                        mediaPlayer.release()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to release MediaPlayer after completion")
                    }
                }

                setOnErrorListener { mediaPlayer, what, extra ->
                    Timber.e("MediaPlayer failed with code: $what, extra: $extra")
                    try {
                        mediaPlayer.release()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to release MediaPlayer during error handling")
                    }
                    true
                }

                start()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize MediaPlayer for resId: $resId")
        }
    }
}