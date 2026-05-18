package com.yugentech.sessions.alerts.service

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.yugentech.sessions.R
import timber.log.Timber

// Service for playing one-off system alert sounds
class SoundService(
    private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    // Helper for start session sound
    fun playStartAlert() {
        Timber.d("Playing session start sound")
        play(R.raw.session_start)
    }

    // Helper for stop session sound
    fun playStopAlert() {
        Timber.d("Playing session stop sound")
        play(R.raw.session_stop)
    }

    // Helper for goal reached / congratulations sound
    fun playGoalReachedAlert() {
        Timber.d("Playing goal reached sound")
        // Note: Using session_end as a fallback, you might want to add a unique sound later
        play(R.raw.session_end)
    }

    // Creates and plays a MediaPlayer, ensuring resources are released afterwards
    private fun play(@RawRes resId: Int) {
        try {
            // Release any existing player to prevent memory leaks and overlapping sounds if needed
            mediaPlayer?.apply {
                try {
                    if (isPlaying) stop()
                    release()
                } catch (e: Exception) {
                    Timber.w(e, "Error releasing existing MediaPlayer")
                }
            }

            mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                setOnCompletionListener { mp ->
                    try {
                        mp.release()
                        if (mediaPlayer == mp) {
                            mediaPlayer = null
                        }
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to release MediaPlayer after completion")
                    }
                }

                setOnErrorListener { mp, what, extra ->
                    Timber.e("MediaPlayer failed with code: $what, extra: $extra")
                    try {
                        mp.release()
                        if (mediaPlayer == mp) {
                            mediaPlayer = null
                        }
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