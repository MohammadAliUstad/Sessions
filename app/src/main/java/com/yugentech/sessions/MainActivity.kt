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
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        Timber.v("MainActivity onCreate: App launching")

        val loginViewModel: LoginViewModel = get()

        splashScreen.setKeepOnScreenCondition {
            loginViewModel.authState.value.isLoading || loginViewModel.showOnboarding.value == null
        }

        setContent {
            val navController = rememberNavController()

            val showOnboarding by loginViewModel.showOnboarding.collectAsStateWithLifecycle()
            val authState by loginViewModel.authState.collectAsStateWithLifecycle()

            val themeViewModel: ThemeViewModel = koinViewModel()
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
                    SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
                },
                navigationBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(scrim = Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
                }
            )

            SessionsTheme(
                themeConfiguration = themeConfiguration
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showOnboarding != null && !authState.isLoading) {
                        AppNavHost(
                            navController = navController,
                            webClientId = getString(R.string.web_client_id),
                            loginViewModel = loginViewModel,
                            showOnboarding = showOnboarding!!,
                            onOnboardingComplete = {
                                loginViewModel.completeOnboarding()
                            }
                        )
                    }
                }
            }
        }
    }
}