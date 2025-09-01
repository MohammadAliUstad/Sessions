@file:Suppress("DEPRECATION")

package com.yugentech.sessions.alerts

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import timber.log.Timber

// Service for triggering haptic feedback across different Android API levels
class HapticService(
    context: Context
) {

    // Lazy initialization of the correct vibrator service based on Android version
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // Triggers feedback: prefers View-based haptics, falls back to raw vibration
    fun performHaptic(view: View? = null) {
        try {
            Timber.v("Triggering haptic feedback")

            if (view != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            20,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to perform haptic feedback")
        }
    }
}