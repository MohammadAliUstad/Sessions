package com.yugentech.sessions.alerts

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import com.yugentech.sessions.alerts.models.BackgroundSound
import timber.log.Timber

class BackgroundSoundService(
    private val context: Context
) {

    private var player: MediaPlayer? = null
    private var nextPlayer: MediaPlayer? = null

    private var currentSound: BackgroundSound = BackgroundSound.NONE
    private var currentAnimator: ValueAnimator? = null
    private var isLooping = false
    private var currentResId: Int? = null

    private val handler = Handler(Looper.getMainLooper())
    private val autoStopRunnable = Runnable { releaseResources() }

    // Constants
    private val DEFAULT_FADE_DURATION = 2000L
    private val PREVIEW_PLAY_TIME = 2000L

    private val focusVolume = 1.0f
    private val breakVolume = 0.2f

    fun play(sound: BackgroundSound) {
        handler.removeCallbacks(autoStopRunnable)

        if (sound == BackgroundSound.NONE) {
            stop()
            return
        }

        // If the same sound is already playing, just ensure volume is correct
        if (isLooping && currentSound == sound) {
            fadeToFocusMode()
            return
        }

        releaseResources()

        sound.resId?.let { resId ->
            try {
                currentSound = sound
                currentResId = resId
                isLooping = true

                // Create and start the main player
                player = MediaPlayer.create(context, resId).apply {
                    setVolume(0f, 0f) // Start silent for fade-in
                    setOnCompletionListener { onPlaybackEnded(it) }
                    start()
                }

                // Immediately prepare the next player for gapless transition
                prepareNextPlayer()

                fadeVolume(from = 0f, to = focusVolume)
            } catch (e: Exception) {
                Timber.e(e, "Failed to start background sound")
            }
        }
    }

    private fun prepareNextPlayer() {
        val resId = currentResId ?: return

        try {
            nextPlayer = MediaPlayer().apply {
                // Use AssetFileDescriptor for better control
                val afd = context.resources.openRawResourceFd(resId)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()

                prepare()

                // Match the volume of current player
                val volume = getCurrentVolume()
                setVolume(volume, volume)
            }

            // THIS IS THE MAGIC: Set next player for gapless transition
            player?.setNextMediaPlayer(nextPlayer)

        } catch (e: Exception) {
            Timber.e(e, "Failed to prepare next MediaPlayer")
        }
    }

    private fun onPlaybackEnded(completedPlayer: MediaPlayer) {
        if (!isLooping) return

        try {
            // Release the completed player
            completedPlayer.setOnCompletionListener(null)
            completedPlayer.release()

            // Swap: next becomes current
            player = nextPlayer
            player?.setOnCompletionListener { onPlaybackEnded(it) }

            // Prepare the next player for the next loop
            prepareNextPlayer()

            // Stability fix for older Android versions (below Marshmallow)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                player?.apply {
                    seekTo(0)
                    stop()
                    prepare()
                    start()
                }
            }

        } catch (e: Exception) {
            Timber.e(e, "Error in playback completion handler")
        }
    }

    fun playPreview(sound: BackgroundSound) {
        handler.removeCallbacks(autoStopRunnable)
        releaseResources()

        if (sound == BackgroundSound.NONE) return

        sound.resId?.let { resId ->
            try {
                isLooping = false
                player = MediaPlayer.create(context, resId).apply {
                    setVolume(focusVolume, focusVolume)
                    start()
                }
                currentSound = sound
                handler.postDelayed(autoStopRunnable, PREVIEW_PLAY_TIME)
            } catch (e: Exception) {
                Timber.e(e, "Preview failed")
            }
        }
    }

    fun fadeToBreakMode() {
        fadeVolume(from = focusVolume, to = breakVolume)
    }

    fun fadeToFocusMode() {
        fadeVolume(from = breakVolume, to = focusVolume)
    }

    fun stop() {
        if (player?.isPlaying == true) {
            fadeVolume(from = getCurrentVolume(), to = 0f) {
                releaseResources()
            }
        } else {
            releaseResources()
        }
    }

    private fun releaseResources() {
        try {
            handler.removeCallbacks(autoStopRunnable)
            currentAnimator?.cancel()
            currentAnimator = null
            isLooping = false
            currentResId = null

            player?.setOnCompletionListener(null)
            player?.stop()
            player?.release()
            player = null

            nextPlayer?.release()
            nextPlayer = null

            currentSound = BackgroundSound.NONE
        } catch (e: Exception) {
            Timber.e(e, "Error releasing resources")
        }
    }

    private fun getCurrentVolume(): Float {
        // Since MediaPlayer doesn't expose volume getter, track it via animator
        return when {
            currentAnimator?.isRunning == true -> currentAnimator?.animatedValue as? Float ?: focusVolume
            else -> focusVolume
        }
    }

    private fun fadeVolume(
        from: Float,
        to: Float,
        duration: Long = DEFAULT_FADE_DURATION,
        onEnd: (() -> Unit)? = null
    ) {
        if (player == null) return

        currentAnimator?.cancel()

        currentAnimator = ValueAnimator.ofFloat(from, to).apply {
            this.duration = duration
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val newVolume = animation.animatedValue as Float
                try {
                    player?.setVolume(newVolume, newVolume)
                    nextPlayer?.setVolume(newVolume, newVolume)
                } catch (e: Exception) {
                    Timber.e(e, "Error adjusting volume")
                }
            }

            if (onEnd != null) {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        onEnd()
                    }
                })
            }

            start()
        }
    }

    fun release() {
        releaseResources()
    }
}