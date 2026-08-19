package com.yugentech.sessions.alerts.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.yugentech.sessions.R
import timber.log.Timber

// Service for playing one-off system alert sounds
class SoundService(
    private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null

    private val alertAudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    fun playStartAlert(onComplete: (() -> Unit)? = null) {
        Timber.d("Playing session start sound")
        play(R.raw.session_start, onComplete)
    }

    fun playStopAlert(onComplete: (() -> Unit)? = null) {
        Timber.d("Playing session stop sound")
        play(R.raw.session_stop, onComplete)
    }

    fun playGoalReachedAlert(onComplete: (() -> Unit)? = null) {
        Timber.d("Playing goal reached sound")
        play(R.raw.session_end, onComplete)
    }

    private fun play(@RawRes resId: Int, onComplete: (() -> Unit)? = null) {
        try {
            mediaPlayer?.apply {
                try {
                    if (isPlaying) stop()
                    release()
                } catch (e: Exception) {
                    Timber.w(e, "Error releasing existing MediaPlayer")
                }
            }
            abandonFocus()

            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(alertAudioAttributes)
                .build()
            focusRequest = request
            audioManager.requestAudioFocus(request)

            mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                setAudioAttributes(alertAudioAttributes)
                setOnCompletionListener { mp ->
                    try {
                        mp.release()
                        if (mediaPlayer == mp) mediaPlayer = null
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to release MediaPlayer after completion")
                    }
                    abandonFocus()
                    onComplete?.invoke()
                }
                setOnErrorListener { mp, what, extra ->
                    Timber.e("MediaPlayer failed with code: $what, extra: $extra")
                    try {
                        mp.release()
                        if (mediaPlayer == mp) mediaPlayer = null
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to release MediaPlayer during error handling")
                    }
                    abandonFocus()
                    onComplete?.invoke()
                    true
                }
                start()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize MediaPlayer for resId: $resId")
            abandonFocus()
            onComplete?.invoke()
        }
    }

    private fun abandonFocus() {
        focusRequest?.let {
            audioManager.abandonAudioFocusRequest(it)
            focusRequest = null
        }
    }
}