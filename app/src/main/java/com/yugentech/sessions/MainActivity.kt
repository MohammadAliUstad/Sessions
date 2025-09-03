package com.yugentech.sessions

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.yugentech.sessions.navigation.AppNavHost
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.utils.SessionsTheme
import com.yugentech.sessions.theme.utils.ThemeMode
import com.yugentech.sessions.user.UserViewModel
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setContent {
            val webClientId = getString(R.string.web_client_id)
            val navController = rememberNavController()
            val loginViewModel: LoginViewModel = koinViewModel()
            splashScreen.setKeepOnScreenCondition {
                loginViewModel.authState.value.isLoading
            }
            val userViewModel: UserViewModel = koinViewModel()
            val homeViewModel: HomeViewModel = koinViewModel()
            val profileViewModel: ProfileViewModel = koinViewModel()
            val themeViewModel: ThemeViewModel = koinViewModel()
            val settingsViewModel: SettingsViewModel = koinViewModel()
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()
            val darkTheme = when (themeConfiguration.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            enableEdgeToEdge(
                statusBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(scrim = Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.WHITE)
                }
            )
            SessionsTheme(
                themeConfiguration = themeConfiguration
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController,
                        webClientId = webClientId,
                        loginViewModel = loginViewModel,
                        userViewModel = userViewModel,
                        homeViewModel = homeViewModel,
                        profileViewModel = profileViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}