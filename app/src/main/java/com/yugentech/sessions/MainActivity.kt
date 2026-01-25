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
import com.yugentech.sessions.navigation.AppNavHost
import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.theme.SessionsTheme
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.models.ThemeMode
import com.yugentech.sessions.viewModels.LoginViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {

    // Tracks if a notification click should trigger navigation to the Home screen
    private var shouldNavigateToHome by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        Timber.v("MainActivity onCreate: App launching")

        handleNavigationIntent(intent)

        val loginViewModel: LoginViewModel = get()

        // Keeps splash screen visible until authentication and onboarding status are loaded
        splashScreen.setKeepOnScreenCondition {
            loginViewModel.authState.value.isLoading || loginViewModel.showOnboarding.value == null
        }

        setContent {
            val navController = rememberNavController()

            val showOnboarding by loginViewModel.showOnboarding.collectAsStateWithLifecycle()

            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()

            // Determine if dark mode should be applied based on user preference or system settings
            val darkTheme = when (themeConfiguration.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            // Configures the status and navigation bars to be transparent for edge-to-edge design
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
                    // Only render content once the onboarding state is determined
                    if (showOnboarding != null) {
                        AppNavHost(
                            navController = navController,
                            webClientId = getString(R.string.web_client_id),
                            loginViewModel = loginViewModel,
                            showOnboarding = showOnboarding!!,
                            onOnboardingComplete = {
                                loginViewModel.completeOnboarding()
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

    // Handles new intents delivered to the activity while it is already running
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNavigationIntent(intent)
    }

    // Checks intent extras to see if the app was opened via a specific notification action
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