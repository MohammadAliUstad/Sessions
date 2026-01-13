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
import com.yugentech.sessions.theme.SessionsTheme
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.models.ThemeMode
import com.yugentech.sessions.viewModels.LoginViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("MainActivity onCreate: App launching")

        val loginViewModel: LoginViewModel = get()

        // Handle Android 12+ Splash Screen and hold it until auth state is resolved
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            loginViewModel.authState.value.isLoading
        }
        setContent {
            val navController = rememberNavController()

            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()

            // Resolve theme mode based on user preference or system default
            val darkTheme = when (themeConfiguration.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            // Configure transparent system bars for edge-to-edge content
            enableEdgeToEdge(
                statusBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(scrim = Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.WHITE)
                },
                navigationBarStyle = if (darkTheme) {
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
                        webClientId = getString(R.string.web_client_id),
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }
}