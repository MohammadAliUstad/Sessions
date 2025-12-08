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

class HapticService(context: Context) {

    // Lazy initialization of the appropriate Vibrator service based on API level
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun performHaptic(view: View? = null) {
        try {
            Timber.v("Triggering haptic feedback")

            if (view != null) {
                // Use View-based feedback if available for better system integration
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                // Fallback to manual vibration for background services or non-view contexts
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
        } catch (e: Exception) {
            // Log as warning since missing haptics isn't a critical crash
            Timber.w(e, "Failed to perform haptic feedback")
        }
    }
}