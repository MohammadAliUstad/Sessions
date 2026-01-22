package com.yugentech.sessions.theme.tokens.dimensions

import androidx.compose.ui.unit.dp

object AppConstants {
    const val EMPTY_STRING = ""
    const val ZERO = 0
    const val LONG = 0L
    const val THREESIXTYF = 360f
    const val ONEF = 1f
    const val ONEPOINT_ZEROFIVEF = 1.05f
    const val ONEPOINT_TWOF = 1.2f
    const val ONEPOINT_FIVEF = 1.5f
    const val TWOF = 2f
    const val ZEROF = 0f
    const val ONE = 1
    const val TWO = 2
    const val EIGHT = 8
    const val TWENTY = 20
    val IMAGE_SIZE = 120.dp
    const val PTHOUSAND = 1000
    const val MTHOUSAND = -1000
    const val DEFAULT_ANIMATION_DURATION = 300
    const val ACTION_START_SESSION = "START_SESSION"
    const val ACTION_STOP_SESSION = "STOP_SESSION"
    const val ACTION_UPDATE_SESSION = "UPDATE_SESSION"
    const val GITHUB_URL = "https://github.com/MohammadAliUstad/Sessions"
    const val PRIVACY_POLICY_URL = "https://sites.google.com/view/sessionsprivacypolicy/home"
    const val TERMS_OF_SERVICE_URL = "https://sites.google.com/view/sessionstermsofservice/home"
    const val KOFI_URL = "https://ko-fi.com/yugentech"
    const val SUPPORT_EMAIL = "mailto:yugentech.kazuki@gmail.com"
    const val FEEDBACK_SUBJECT = "App Feedback: Sessions"
    private const val APP_PACKAGE_NAME = "com.yugentech.sessions"

    // 2. The Link (Reuse this!)
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=$APP_PACKAGE_NAME"

    // 3. The Share Message
    // Note: We combine them here or in the UI code.
    // It's often safer to combine them in the Intent to ensure the link is at the end.
    const val SHARE_MESSAGE = "Check out Sessions, a minimal pomodoro focus app\n$PLAY_STORE_URL"
    const val MARKET_URL = "market://details?id=com.yugentech.sessions"
}