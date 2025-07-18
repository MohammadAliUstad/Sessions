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
import androidx.navigation.compose.rememberNavController
import com.yugentech.sessions.authentication.AuthViewModel
import com.yugentech.sessions.navigation.AppNavHost
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.status.StatusViewModel
import com.yugentech.sessions.ui.theme.SessionsTheme
import com.yugentech.sessions.viewModels.LeaderboardViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val authViewModel: AuthViewModel = koinViewModel()
                    val sessionViewModel: SessionViewModel = koinViewModel()
                    val statusViewModel: StatusViewModel = koinViewModel()
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
    }
}