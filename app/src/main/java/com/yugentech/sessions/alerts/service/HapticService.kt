@file:Suppress("DEPRECATION")

package com.yugentech.sessions.alerts.service

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
        performHapticInternal(view, isSpecial = false)
    }

    // Triggers a more noticeable haptic sequence for completion
    fun performCompletionHaptic(view: View? = null) {
        performHapticInternal(view, isSpecial = true)
    }

    private fun performHapticInternal(view: View?, isSpecial: Boolean) {
        try {
            Timber.v("Triggering haptic feedback (special: $isSpecial)")

            if (view != null) {
                if (isSpecial) {
                    // Use a more refined confirmation pulse for goal completion
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    } else {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Use a lighter "clock tick" or "gesture" pulse for standard interactions
                        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                    } else {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (isSpecial) {
                        // Refined "Success" sequence: two short sharp pulses
                        vibrator.vibrate(
                            VibrationEffect.createWaveform(
                                longArrayOf(0, 50, 100, 50),
                                -1
                            )
                        )
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // Single sharp click
                            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                        } else {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    15,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        }
                    }
                } else {
                    if (isSpecial) {
                        vibrator.vibrate(longArrayOf(0, 50, 100, 50), -1)
                    } else {
                        vibrator.vibrate(15)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to perform haptic feedback")
        }
    }
}