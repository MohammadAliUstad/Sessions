package com.yugentech.sessions

import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.yugentech.sessions.auth.viewmodel.AuthViewModel
import com.yugentech.sessions.localization.AppLocale
import com.yugentech.sessions.navigation.host.AppNavHost
import com.yugentech.sessions.theme.SessionsTheme
import com.yugentech.sessions.theme.ThemeRevealHost
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {

    // Held by the activity's ViewModelStore, so it survives a recreation (for example when the
    // system recreates the screen for a language change) instead of starting auth over
    private val authViewModel: AuthViewModel by viewModel()

    // Below Android 13, the language picked in the app (see AppLocale); from 13 the system's
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocale.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // The splash only holds for its animation on a real launch. When the activity is recreated
        // (savedInstanceState != null) there is no launch animation to wait for, and holding it
        // would show a blank splash window over a screen that is already ready
        val isRecreation = savedInstanceState != null
        val animationReady = MutableStateFlow(isRecreation)
        if (!isRecreation) {
            lifecycleScope.launch {
                delay(1000.milliseconds)
                animationReady.value = true
            }
        }

        super.onCreate(savedInstanceState)
        Timber.v("MainActivity onCreate: %s", if (isRecreation) "recreated" else "App launching")

        splashScreen.setKeepOnScreenCondition {
            !animationReady.value ||
                    authViewModel.authState.value.isInitializing ||
                    authViewModel.showOnboarding.value == null
        }

        setContent {
            val navController = rememberNavController()

            val showOnboarding by authViewModel.showOnboarding.collectAsStateWithLifecycle()
            val authState by authViewModel.authState.collectAsStateWithLifecycle()

            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()
            val hapticsEnabled by themeViewModel.hapticsEnabled.collectAsStateWithLifecycle()

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
                themeConfiguration = themeConfiguration,
                hapticsEnabled = hapticsEnabled
            ) {
                // Outermost, so a light/dark wipe covers the whole screen.
                ThemeRevealHost {
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}