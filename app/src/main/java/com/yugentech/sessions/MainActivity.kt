package com.yugentech.sessions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.yugentech.sessions.navigation.AppNavHost
import com.yugentech.sessions.sessions.SessionsViewModel
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.utils.SessionsTheme
import com.yugentech.sessions.user.UserViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val webClientId = getString(R.string.web_client_id)
            val navController = rememberNavController()
            val loginViewModel: LoginViewModel = koinViewModel()
            val sessionsViewModel: SessionsViewModel = koinViewModel()
            val userViewModel: UserViewModel = koinViewModel()
            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()
            SessionsTheme(
                themeConfiguration = themeConfiguration
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        sessionsViewModel = sessionsViewModel,
                        webClientId = webClientId,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }
}