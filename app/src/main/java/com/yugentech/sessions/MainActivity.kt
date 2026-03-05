package com.yugentech.sessions

import android.content.Intent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.yugentech.sessions.navigation.host.AppNavHost
import com.yugentech.sessions.notification.service.NotificationService
import com.yugentech.sessions.theme.SessionsTheme
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private var shouldNavigateToHome by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        Timber.v("MainActivity onCreate: App launching")

        handleNavigationIntent(intent)

        val authViewModel: AuthViewModel = get()

        splashScreen.setKeepOnScreenCondition {
            authViewModel.authState.value.isInitializing || authViewModel.showOnboarding.value == null
        }

        setContent {
            val navController = rememberNavController()

            val showOnboarding by authViewModel.showOnboarding.collectAsStateWithLifecycle()
            val authState by authViewModel.authState.collectAsStateWithLifecycle()

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
                    if (showOnboarding != null && !authState.isInitializing) {
                        AppNavHost(
                            navController = navController,
                            webClientId = getString(R.string.web_client_id),
                            authViewModel = authViewModel,
                            showOnboarding = showOnboarding!!,
                            onOnboardingComplete = {
                                authViewModel.completeOnboarding()
                            },
                            shouldNavigateToHome = shouldNavigateToHome,
                            onNavigatedToHome = {
                                shouldNavigateToHome = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNavigationIntent(intent)
    }

    private fun handleNavigationIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(
                NotificationService.EXTRA_NAVIGATE_TO_HOME,
                false
            ) == true
        ) {
            Timber.d("Navigation to home requested from notification")
            shouldNavigateToHome = true
        }
    }
}