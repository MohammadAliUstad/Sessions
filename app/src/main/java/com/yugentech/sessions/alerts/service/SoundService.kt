package com.yugentech.sessions.alerts.service

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.yugentech.sessions.R
import timber.log.Timber

// Plays one-off alert sounds using SoundPool so all clips are pre-loaded into
// memory at startup, giving near-zero per-play overhead.
class SoundService(private val context: Context) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(audioAttributes)
        .build()

    private val soundIds = mutableMapOf<Int, Int>()

    init {
        preload(R.raw.session_start)
        preload(R.raw.session_stop)
        preload(R.raw.session_end)
    }

    private fun preload(@RawRes resId: Int) {
        soundIds[resId] = soundPool.load(context, resId, 1)
    }

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
        val soundId = soundIds[resId] ?: run {
            Timber.w("Sound not preloaded for resId: $resId")
            onComplete?.invoke()
            return
        }
        try {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            onComplete?.invoke()
        } catch (e: Exception) {
            Timber.e(e, "Failed to play sound for resId: $resId")
            onComplete?.invoke()
        }
    }

    fun release() {
        soundPool.release()
    }
}
