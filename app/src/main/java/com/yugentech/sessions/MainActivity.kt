package com.yugentech.sessions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.yugentech.sessions.authentication.AuthViewModel
import com.yugentech.sessions.navigation.AppNavHost
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.status.StatusViewModel
import com.yugentech.sessions.ui.theme.SessionsTheme
import com.yugentech.sessions.viewModels.LeaderboardViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private lateinit var statusViewModel: StatusViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionViewModel: SessionViewModel // Add this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate")
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            SessionsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val webClientId = getString(R.string.web_client_id)
                    val navController = rememberNavController()
                    authViewModel = koinViewModel()
                    sessionViewModel = koinViewModel()
                    statusViewModel = koinViewModel()
                    val leaderboardViewModel: LeaderboardViewModel = koinViewModel()

                    AppNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        sessionViewModel = sessionViewModel,
                        statusViewModel = statusViewModel,
                        leaderboardViewModel = leaderboardViewModel,
                        webClientId = webClientId
                    )
                }
            }
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                performCleanup()
            }
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        if (::statusViewModel.isInitialized) {
            statusViewModel.setUserStudyStatus(getCurrentUserId().toString(), false)
        }
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart")
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    private fun performCleanup() {
        val userId = getCurrentUserId()
        if (userId != null) {
            statusViewModel.cleanupOnAppExit(applicationContext, userId)
        }
    }

    private fun getCurrentUserId(): String? {
        return if (::authViewModel.isInitialized) {
            authViewModel.userId.value
        } else {
            null
        }
    }
}