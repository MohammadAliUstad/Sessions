package com.yugentech.sessions

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.yugentech.sessions.navigation.AppNavHost
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.SessionsTheme
import com.yugentech.sessions.theme.utils.ThemeMode
import com.yugentech.sessions.user.UserViewModel
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginViewModel: LoginViewModel = get()

        // Show splash screen until auth state is ready
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            loginViewModel.authState.value.isLoading
        }

        setContent {
            val navController = rememberNavController()

            // ViewModels
            val userViewModel: UserViewModel = koinViewModel()
            val homeViewModel: HomeViewModel = koinViewModel()
            val profileViewModel: ProfileViewModel = koinViewModel()
            val notificationsViewModel: NotificationsViewModel = koinViewModel()
            val themeViewModel: ThemeViewModel = koinViewModel()
            val settingsViewModel: SettingsViewModel = koinViewModel()

            // Observe theme mode
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()
            val darkTheme = when (themeConfiguration.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            // Enable edge-to-edge with dynamic system bars
            enableEdgeToEdge(
                statusBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(scrim = Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.WHITE)
                }
            )

            // Apply adaptive theme (includes design tokens automatically)
            SessionsTheme(themeConfiguration = themeConfiguration) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController,
                        webClientId = getString(R.string.web_client_id),
                        loginViewModel = loginViewModel,
                        userViewModel = userViewModel,
                        homeViewModel = homeViewModel,
                        profileViewModel = profileViewModel,
                        settingsViewModel = settingsViewModel,
                        notificationsViewModel = notificationsViewModel
                    )

                    requestNotificationPermission()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}