package com.yugentech.sessions

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
        // TEMP: language-switch flicker diagnosis. What changed since this process's last screen,
        // if the system rebuilt it rather than handing the change over in place
        Log.w("LocaleDiag", if (isRecreation) "onCreate (recreated)" else "onCreate (fresh launch)")
        lastConfiguration?.let {
            Log.w("LocaleDiag", "recreated, changed: " + describeConfigDiff(it.diff(resources.configuration)))
        }
        lastConfiguration = Configuration(resources.configuration)

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

    // TEMP: language-switch flicker diagnosis
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.w(
            "LocaleDiag",
            "handled in place, changed: " + (lastConfiguration?.let { describeConfigDiff(it.diff(newConfig)) } ?: "?")
        )
        lastConfiguration = Configuration(newConfig)
    }

    private companion object {
        /** The configuration the last screen in this process had; outlives a recreation. */
        var lastConfiguration: Configuration? = null

        fun describeConfigDiff(diff: Int): String {
            val names = listOf(
                ActivityInfo.CONFIG_MCC to "mcc",
                ActivityInfo.CONFIG_MNC to "mnc",
                ActivityInfo.CONFIG_LOCALE to "locale",
                ActivityInfo.CONFIG_TOUCHSCREEN to "touchscreen",
                ActivityInfo.CONFIG_KEYBOARD to "keyboard",
                ActivityInfo.CONFIG_KEYBOARD_HIDDEN to "keyboardHidden",
                ActivityInfo.CONFIG_NAVIGATION to "navigation",
                ActivityInfo.CONFIG_ORIENTATION to "orientation",
                ActivityInfo.CONFIG_SCREEN_LAYOUT to "screenLayout",
                ActivityInfo.CONFIG_UI_MODE to "uiMode",
                ActivityInfo.CONFIG_SCREEN_SIZE to "screenSize",
                ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE to "smallestScreenSize",
                ActivityInfo.CONFIG_DENSITY to "density",
                ActivityInfo.CONFIG_LAYOUT_DIRECTION to "layoutDirection",
                ActivityInfo.CONFIG_COLOR_MODE to "colorMode",
                ActivityInfo.CONFIG_FONT_SCALE to "fontScale",
                0x10000000 to "fontWeightAdjustment",
                0x8000 to "grammaticalGender",
                0x80000000.toInt() to "assetsPaths"
            )
            val changed = names.filter { (flag, _) -> diff and flag != 0 }.map { it.second }
            val known = names.fold(0) { acc, (flag, _) -> acc or flag }
            val unknown = diff and known.inv()
            return (changed + if (unknown != 0) listOf("0x" + Integer.toHexString(unknown)) else emptyList())
                .ifEmpty { listOf("nothing") }
                .joinToString("|")
        }
    }
}