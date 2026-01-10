package com.yugentech.sessions.alerts.alertsDatastore.backgroundSounds

import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaPlayer
import android.view.animation.LinearInterpolator
import timber.log.Timber

class BackgroundSoundService(
    private val context: Context
) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentSound: BackgroundSound = BackgroundSound.NONE

    private val focusVolume = 1.0f
    private val breakVolume = 0.2f

    fun play(sound: BackgroundSound) {

        if (sound == BackgroundSound.NONE) {
            stop()
            return
        }

        if (mediaPlayer?.isPlaying == true && currentSound == sound) {
            return
        }

        stop()

        sound.resId?.let { resId ->
            try {
                mediaPlayer = MediaPlayer.create(context, resId).apply {
                    isLooping = true
                    setVolume(0f, 0f)
                    start()
                }
                currentSound = sound

                fadeVolume(from = 0f, to = focusVolume)
            } catch (e: Exception) {
                Timber.e(e, "Failed to start background sound")
            }
        }
    }

    fun fadeToBreakMode() {
        fadeVolume(from = focusVolume, to = breakVolume)
    }

    fun fadeToFocusMode() {
        fadeVolume(from = breakVolume, to = focusVolume)
    }

    private fun fadeVolume(from: Float, to: Float) {
        if (mediaPlayer == null || !mediaPlayer!!.isPlaying) return

        val animator = ValueAnimator.ofFloat(from, to)
        animator.duration = 2000
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val volume = animation.animatedValue as Float
            try {
                mediaPlayer?.setVolume(volume, volume)
            } catch (e: Exception) {
                Timber.e(e, "Error adjusting volume")
            }
        }

        animator.start()
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
            currentSound = BackgroundSound.NONE
        } catch (e: Exception) {
            Timber.e(e, "Error stopping background sound")
        }
    }
}