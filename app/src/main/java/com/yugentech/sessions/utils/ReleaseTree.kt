package com.yugentech.sessions.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // 1. IGNORE Verbose, Debug, and Info logs in production
        // We only care about Warnings and Errors to save data/battery.
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        // 2. PASS LOGS TO CRASHLYTICS
        val crashlytics = FirebaseCrashlytics.getInstance()

        // "Breadcrumbs": This adds the message to the crash report's history
        // so you can see what the user was doing before the crash.
        crashlytics.log(message)

        // 3. REPORT EXCEPTIONS
        // If there is an actual Throwable (Exception), record it explicitly.
        if (t != null) {
            crashlytics.recordException(t)
        }
    }
}