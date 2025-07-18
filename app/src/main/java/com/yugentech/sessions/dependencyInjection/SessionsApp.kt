package com.yugentech.sessions.dependencyInjection

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.firebase.FirebaseApp
import com.yugentech.sessions.dependencyInjection.modules.authModule
import com.yugentech.sessions.dependencyInjection.modules.leaderboardModule
import com.yugentech.sessions.dependencyInjection.modules.sessionModule
import com.yugentech.sessions.dependencyInjection.modules.statusModule
import com.yugentech.sessions.dependencyInjection.modules.userModule
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.status.StatusViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class SessionsApp : Application(), Application.ActivityLifecycleCallbacks, LifecycleOwner {

    private val statusViewModel: StatusViewModel by inject()
    private val sessionViewModel: SessionViewModel by inject()

    private var activeActivities = 0
    private var isAppVisible = false
    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@SessionsApp)

            modules(
                authModule,
                sessionModule,
                statusModule,
                userModule,
                leaderboardModule
            )
        }

        registerActivityLifecycleCallbacks(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activeActivities++
        updateAppVisibility()
    }

    override fun onActivityStarted(activity: Activity) {
        activeActivities++
        updateAppVisibility()
    }

    override fun onActivityResumed(activity: Activity) {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        isAppVisible = true
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        activeActivities--
        updateAppVisibility()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (activeActivities == 0) {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
            checkAppState()
        }
    }

    private fun updateAppVisibility() {
        val wasVisible = isAppVisible
        isAppVisible = activeActivities > 0

        if (wasVisible && !isAppVisible) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            checkAppState()
        } else if (!wasVisible && isAppVisible) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    private fun checkAppState() {
        try {
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val appTasks = activityManager.appTasks

            if (appTasks.isEmpty() || appTasks.all { it.taskInfo == null }) {
                sessionViewModel.stopTimer()
                sessionViewModel.resetTimer()
                statusViewModel.cleanup()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}