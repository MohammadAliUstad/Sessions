package com.yugentech.sessions.alerts

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.yugentech.sessions.alerts.models.BackgroundSound
import timber.log.Timber

class BackgroundSoundService(
    private val context: Context
) {

    private var player1: ExoPlayer? = null
    private var player2: ExoPlayer? = null
    private var isPlayer1Active = true

    private var currentSound: BackgroundSound = BackgroundSound.NONE
    private var userVolumeAnimator: ValueAnimator? = null
    private var crossfadeAnimator: ValueAnimator? = null
    private var isLooping = false
    private var positionCheckRunnable: Runnable? = null
    private var hasTriggeredSwitch = false

    private val handler = Handler(Looper.getMainLooper())
    private val autoStopRunnable = Runnable { releaseResources() }

    // Constants
    private val DEFAULT_FADE_DURATION = 2000L
    private val PREVIEW_PLAY_TIME = 2000L
    private val CROSSFADE_START_BEFORE_END_MS = 2000L // Start crossfade 2 seconds before end
    private val CROSSFADE_DURATION_MS = 2000L // 2 second crossfade

    private val focusVolume = 1.0f
    private val breakVolume = 0.2f
    private var targetVolume = focusVolume // Track what the user wants (focus/break mode)

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
                val uri = "android.resource://${context.packageName}/$resId"
                currentSound = sound
                isLooping = true
                targetVolume = focusVolume

                // Create both players
                player1 = createPlayer(uri)
                player2 = createPlayer(uri)

                // Start player1
                player1?.apply {
                    volume = 0f
                    playWhenReady = true
                }

                isPlayer1Active = true
                hasTriggeredSwitch = false

                // Start monitoring playback position
                startPositionMonitoring(uri)

                fadeUserVolume(from = 0f, to = focusVolume)
            } catch (e: Exception) {
                Timber.e(e, "Failed to start background sound")
            }
        }
    }

    private fun createPlayer(uri: String): ExoPlayer {
        return ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    private fun startPositionMonitoring(uri: String) {
        hasTriggeredSwitch = false

        positionCheckRunnable = object : Runnable {
            override fun run() {
                if (!isLooping) return

                try {
                    val activePlayer = if (isPlayer1Active) player1 else player2
                    val duration = activePlayer?.duration ?: 0
                    val currentPosition = activePlayer?.currentPosition ?: 0

                    // When we're CROSSFADE_START_BEFORE_END_MS before the end, start crossfade
                    if (!hasTriggeredSwitch && duration > 0 &&
                        currentPosition >= (duration - CROSSFADE_START_BEFORE_END_MS)
                    ) {

                        hasTriggeredSwitch = true

                        if (isPlayer1Active) {
                            crossfadeToPlayer2(uri)
                        } else {
                            crossfadeToPlayer1(uri)
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error in position monitoring")
                }

                // Check every 100ms
                handler.postDelayed(this, 100)
            }
        }

        handler.post(positionCheckRunnable!!)
    }

    private fun crossfadeToPlayer2(uri: String) {
        if (!isLooping) return

        Timber.d("Starting crossfade to Player 2")

        // Start player2 at 0 volume
        player2?.apply {
            volume = 0f
            seekTo(0)
            playWhenReady = true
        }

        // Crossfade: player1 volume down, player2 volume up
        crossfadeAnimator?.cancel()
        crossfadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = CROSSFADE_DURATION_MS
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                try {
                    // Equal-power crossfade using cosine curves
                    // This maintains constant perceived loudness
                    val fadeOutGain = kotlin.math.cos(progress * Math.PI / 2.0).toFloat()
                    val fadeInGain = kotlin.math.sin(progress * Math.PI / 2.0).toFloat()

                    // Player1 fades out with cosine curve
                    player1?.volume = targetVolume * fadeOutGain
                    // Player2 fades in with sine curve
                    player2?.volume = targetVolume * fadeInGain
                } catch (e: Exception) {
                    Timber.e(e, "Error during crossfade")
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!isLooping) return

                    // Crossfade complete - stop and reset player1
                    player1?.apply {
                        stop()
                        seekTo(0)
                        prepare()
                    }

                    // Player2 is now active
                    isPlayer1Active = false

                    // Restart position monitoring for player2
                    positionCheckRunnable?.let { handler.removeCallbacks(it) }
                    startPositionMonitoring(uri)
                }
            })

            start()
        }
    }

    private fun crossfadeToPlayer1(uri: String) {
        if (!isLooping) return

        Timber.d("Starting crossfade to Player 1")

        // Start player1 at 0 volume
        player1?.apply {
            volume = 0f
            seekTo(0)
            playWhenReady = true
        }

        // Crossfade: player2 volume down, player1 volume up
        crossfadeAnimator?.cancel()
        crossfadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = CROSSFADE_DURATION_MS
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                try {
                    // Equal-power crossfade using cosine curves
                    // This maintains constant perceived loudness
                    val fadeOutGain = kotlin.math.cos(progress * Math.PI / 2.0).toFloat()
                    val fadeInGain = kotlin.math.sin(progress * Math.PI / 2.0).toFloat()

                    // Player2 fades out with cosine curve
                    player2?.volume = targetVolume * fadeOutGain
                    // Player1 fades in with sine curve
                    player1?.volume = targetVolume * fadeInGain
                } catch (e: Exception) {
                    Timber.e(e, "Error during crossfade")
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!isLooping) return

                    // Crossfade complete - stop and reset player2
                    player2?.apply {
                        stop()
                        seekTo(0)
                        prepare()
                    }

                    // Player1 is now active
                    isPlayer1Active = true

                    // Restart position monitoring for player1
                    positionCheckRunnable?.let { handler.removeCallbacks(it) }
                    startPositionMonitoring(uri)
                }
            })

            start()
        }
    }

    fun playPreview(sound: BackgroundSound) {
        handler.removeCallbacks(autoStopRunnable)
        releaseResources()

        if (sound == BackgroundSound.NONE) return

        sound.resId?.let { resId ->
            try {
                val uri = "android.resource://${context.packageName}/$resId"
                isLooping = false
                targetVolume = focusVolume
                player1 = ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(uri))
                    volume = 0f // Start at 0 for fade in
                    prepare()
                    playWhenReady = true
                }
                currentSound = sound

                // Fade in (300ms)
                val fadeInDuration = 300L
                userVolumeAnimator?.cancel()
                userVolumeAnimator = ValueAnimator.ofFloat(0f, focusVolume).apply {
                    duration = fadeInDuration
                    interpolator = LinearInterpolator()

                    addUpdateListener { animation ->
                        val newVolume = animation.animatedValue as Float
                        try {
                            player1?.volume = newVolume
                        } catch (e: Exception) {
                            Timber.e(e, "Error fading in preview")
                        }
                    }

                    start()
                }

                // Schedule fade out before stopping (last 300ms)
                val fadeOutDuration = 300L
                val fadeOutStartTime = PREVIEW_PLAY_TIME - fadeOutDuration

                handler.postDelayed({
                    // Fade out
                    userVolumeAnimator?.cancel()
                    userVolumeAnimator = ValueAnimator.ofFloat(focusVolume, 0f).apply {
                        duration = fadeOutDuration
                        interpolator = LinearInterpolator()

                        addUpdateListener { animation ->
                            val newVolume = animation.animatedValue as Float
                            try {
                                player1?.volume = newVolume
                            } catch (e: Exception) {
                                Timber.e(e, "Error fading out preview")
                            }
                        }

                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                releaseResources()
                            }
                        })

                        start()
                    }
                }, fadeOutStartTime)

            } catch (e: Exception) {
                Timber.e(e, "Preview failed")
            }
        }
    }

    fun fadeToBreakMode() {
        fadeUserVolume(from = focusVolume, to = breakVolume)
    }

    fun fadeToFocusMode() {
        fadeUserVolume(from = breakVolume, to = focusVolume)
    }

    fun stop() {
        val activePlayer = if (isPlayer1Active) player1 else player2
        if (activePlayer?.isPlaying == true) {
            fadeUserVolume(from = targetVolume, to = 0f) {
                releaseResources()
            }
        } else {
            releaseResources()
        }
    }

    private fun releaseResources() {
        try {
            positionCheckRunnable?.let { handler.removeCallbacks(it) }
            handler.removeCallbacks(autoStopRunnable)
            userVolumeAnimator?.cancel()
            crossfadeAnimator?.cancel()
            userVolumeAnimator = null
            crossfadeAnimator = null
            isLooping = false

            player1?.stop()
            player1?.release()
            player1 = null

            player2?.stop()
            player2?.release()
            player2 = null

            currentSound = BackgroundSound.NONE
            isPlayer1Active = true
            hasTriggeredSwitch = false
        } catch (e: Exception) {
            Timber.e(e, "Error releasing resources")
        }
    }

    private fun fadeUserVolume(
        from: Float,
        to: Float,
        duration: Long = DEFAULT_FADE_DURATION,
        onEnd: (() -> Unit)? = null
    ) {
        userVolumeAnimator?.cancel()

        userVolumeAnimator = ValueAnimator.ofFloat(from, to).apply {
            this.duration = duration
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val newVolume = animation.animatedValue as Float
                targetVolume = newVolume
                try {
                    // Only adjust the active player during user volume changes
                    if (isPlayer1Active) {
                        player1?.volume = newVolume
                    } else {
                        player2?.volume = newVolume
                    }
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