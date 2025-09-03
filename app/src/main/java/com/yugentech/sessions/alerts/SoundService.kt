package com.yugentech.sessions.alerts

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.yugentech.sessions.R

class SoundService(
    private val context: Context
) {
    fun playSessionStartSound() {
        play(R.raw.session_start)
    }

    fun playSessionStopSound() {
        play(R.raw.session_end)
    }

    private fun play(@RawRes resId: Int) {
        try {
            MediaPlayer.create(context, resId)?.apply {
                setOnCompletionListener { mediaPlayer ->
                    try {
                        mediaPlayer.release()
                    } catch (_: Exception) {
                        // Silently handle release errors
                    }
                }
                setOnErrorListener { mediaPlayer, _, _ ->
                    try {
                        mediaPlayer.release()
                    } catch (_: Exception) {
                        // Silently handle release errors
                    }
                    true // Return true to indicate error was handled
                }
                start()
            }
        } catch (_: Exception) {
            // Silently handle creation/start errors
        }
    }
}