package com.yugentech.sessions.alerts

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.yugentech.sessions.alerts.models.BackgroundSound
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

// Manages playback of looping background sounds using two players for gapless crossfading
class BackgroundSoundService(private val context: Context) {

    private var activePlayer: ExoPlayer? = null
    private var nextPlayer: ExoPlayer? = null
    private var currentSound = BackgroundSound.NONE
    private var volumeAnimator: ValueAnimator? = null
    private var crossfadeAnimator: ValueAnimator? = null
    private var isLooping = false
    private var positionMonitor: Runnable? = null
    private var crossfadeScheduled = false
    private var targetVolume = FOCUS_VOLUME
    private var isStopping = false

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val FADE_DURATION = 1500L
        private const val CROSSFADE_START_OFFSET = 2000L
        private const val CROSSFADE_DURATION = 2000L
        private const val PREVIEW_DURATION = 2000L
        private const val PREVIEW_FADE_DURATION = 500L
        private const val POSITION_CHECK_INTERVAL = 100L
        private const val FOCUS_VOLUME = 1.0f
        private const val BREAK_VOLUME = 0.1f
    }

    // Main entry point to start playing a specific sound
    fun play(sound: BackgroundSound) {
        Timber.d("play() called with sound: ${sound.id}")

        if (sound == BackgroundSound.NONE) {
            stop()
            return
        }

        if (isStopping) {
            Timber.d("Cancelling ongoing stop operation")
            isStopping = false
        }

        // If the same sound is already playing, just ensure volume is up
        if (isLooping && currentSound == sound) {
            Timber.d("Same sound already playing, fading to focus mode")
            fadeToFocusMode()
            return
        }

        Timber.d("Starting new sound: ${sound.id}")
        release()

        sound.resId?.let { resId ->
            try {
                val uri = "android.resource://${context.packageName}/$resId"
                currentSound = sound
                isLooping = true
                targetVolume = FOCUS_VOLUME
                crossfadeScheduled = false
                isStopping = false

                // Initialize two players for seamless looping via crossfading
                activePlayer = createPlayer(uri).apply {
                    volume = 0f
                    playWhenReady = true
                }
                nextPlayer = createPlayer(uri)

                startPositionMonitoring(uri)
                fadeVolume(0f, FOCUS_VOLUME)
            } catch (e: Exception) {
                Timber.e(e, "Failed to start background sound")
                release()
            }
        }
    }

    // Plays a short clip of the sound for user selection, then fades out
    fun playPreview(sound: BackgroundSound) {
        Timber.d("playPreview() called with sound: ${sound.id}")
        handler.removeCallbacksAndMessages(null)
        release()

        if (sound == BackgroundSound.NONE) return

        sound.resId?.let { resId ->
            try {
                val uri = "android.resource://${context.packageName}/$resId"
                currentSound = sound
                isLooping = false
                isStopping = false

                activePlayer = ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(uri))
                    volume = 0f
                    repeatMode = Player.REPEAT_MODE_OFF
                    prepare()
                    playWhenReady = true
                }

                // Fade in
                volumeAnimator = ValueAnimator.ofFloat(0f, FOCUS_VOLUME).apply {
                    duration = PREVIEW_FADE_DURATION
                    interpolator = LinearInterpolator()
                    addUpdateListener { activePlayer?.volume = it.animatedValue as Float }
                    start()
                }

                // Schedule fade out and cleanup
                handler.postDelayed({
                    volumeAnimator?.cancel()
                    volumeAnimator =
                        ValueAnimator.ofFloat(activePlayer?.volume ?: FOCUS_VOLUME, 0f).apply {
                            duration = PREVIEW_FADE_DURATION
                            interpolator = LinearInterpolator()
                            addUpdateListener { activePlayer?.volume = it.animatedValue as Float }
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) = release()
                            })
                            start()
                        }
                }, PREVIEW_DURATION - PREVIEW_FADE_DURATION)

            } catch (e: Exception) {
                Timber.e(e, "Preview failed")
                release()
            }
        }
    }

    // Lowers volume during break sessions
    fun fadeToBreakMode() {
        Timber.d("fadeToBreakMode() called")
        fadeVolume(FOCUS_VOLUME, BREAK_VOLUME)
    }

    // Restores full volume for focus sessions
    fun fadeToFocusMode() {
        Timber.d("fadeToFocusMode() called")
        fadeVolume(BREAK_VOLUME, FOCUS_VOLUME)
    }

    // Gradually fades out audio and releases resources
    fun stop() {
        Timber.d("stop() called")

        if (activePlayer?.isPlaying == true) {
            isStopping = true
            fadeVolume(targetVolume, 0f) {
                if (isStopping) {
                    Timber.d("Fade complete, releasing player")
                    release()
                } else {
                    Timber.d("Fade complete, but stop was cancelled - keeping player")
                }
            }
        } else {
            release()
        }
    }

    // Helper to build a simple ExoPlayer instance
    private fun createPlayer(uri: String) = ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(uri))
        prepare()
    }

    // Monitors playback progress to trigger crossfade near the end of the track
    private fun startPositionMonitoring(uri: String) {
        crossfadeScheduled = false

        positionMonitor = object : Runnable {
            override fun run() {
                if (!isLooping) return

                try {
                    val duration = activePlayer?.duration ?: 0
                    val position = activePlayer?.currentPosition ?: 0

                    if (!crossfadeScheduled && duration > 0 && position >= (duration - CROSSFADE_START_OFFSET)) {
                        crossfadeScheduled = true
                        performCrossfade(uri)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error in position monitoring")
                }

                handler.postDelayed(this, POSITION_CHECK_INTERVAL)
            }
        }

        handler.post(positionMonitor!!)
    }

    // Seamlessly transitions from active player to next player using equal-power crossfade
    private fun performCrossfade(uri: String) {
        if (!isLooping) return

        Timber.d("Starting crossfade")

        nextPlayer?.apply {
            volume = 0f
            seekTo(0)
            playWhenReady = true
        }

        crossfadeAnimator?.cancel()
        crossfadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = CROSSFADE_DURATION
            interpolator = LinearInterpolator()

            // Calculate equal-power gain for smooth audio transition
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                try {
                    val fadeOutGain = cos(progress * Math.PI / 2.0).toFloat()
                    val fadeInGain = sin(progress * Math.PI / 2.0).toFloat()

                    activePlayer?.volume = targetVolume * fadeOutGain
                    nextPlayer?.volume = targetVolume * fadeInGain
                } catch (e: Exception) {
                    Timber.e(e, "Error during crossfade")
                }
            }

            // Swap players when transition completes so the cycle continues
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!isLooping) return

                    activePlayer?.apply {
                        stop()
                        seekTo(0)
                        prepare()
                    }

                    val temp = activePlayer
                    activePlayer = nextPlayer
                    nextPlayer = temp

                    positionMonitor?.let { handler.removeCallbacks(it) }
                    startPositionMonitoring(uri)
                }
            })

            start()
        }
    }

    // Generic helper to animate volume changes
    private fun fadeVolume(
        from: Float,
        to: Float,
        duration: Long = FADE_DURATION,
        onEnd: (() -> Unit)? = null
    ) {
        volumeAnimator?.cancel()
        volumeAnimator?.removeAllListeners()
        volumeAnimator?.removeAllUpdateListeners()

        volumeAnimator = ValueAnimator.ofFloat(from, to).apply {
            this.duration = duration
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val newVolume = animation.animatedValue as Float
                targetVolume = newVolume
                try {
                    activePlayer?.volume = newVolume
                } catch (e: Exception) {
                    Timber.e(e, "Error adjusting volume")
                }
            }

            onEnd?.let {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) = it()
                })
            }

            start()
        }
    }

    // Cleans up all players, animators, and handlers to prevent leaks
    fun release() {
        Timber.d("release() called")
        try {
            positionMonitor?.let { handler.removeCallbacks(it) }

            volumeAnimator?.cancel()
            volumeAnimator?.removeAllListeners()
            volumeAnimator?.removeAllUpdateListeners()

            crossfadeAnimator?.cancel()
            crossfadeAnimator?.removeAllListeners()
            crossfadeAnimator?.removeAllUpdateListeners()

            volumeAnimator = null
            crossfadeAnimator = null
            isLooping = false
            isStopping = false

            activePlayer?.stop()
            activePlayer?.release()
            activePlayer = null

            nextPlayer?.stop()
            nextPlayer?.release()
            nextPlayer = null

            currentSound = BackgroundSound.NONE
            crossfadeScheduled = false
        } catch (e: Exception) {
            Timber.e(e, "Error releasing resources")
        }
    }
}