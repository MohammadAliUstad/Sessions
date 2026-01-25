package com.yugentech.sessions.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        // Skips verbose, debug, and info logs in production to save resources
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        val crashlytics = FirebaseCrashlytics.getInstance()

        crashlytics.log(message)

        // Reports exceptions directly to Firebase Crashlytics for tracking
        if (t != null) {
            crashlytics.recordException(t)
        }
    }
}