package com.yugentech.sessions.ui.auth.onboardingScreen.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import com.yugentech.sessions.R

// The onboarding flow, in order. The pager and progress indicator size themselves from this
// list, so adding or removing a page only needs a change here.
val onboardingPages = listOf(
    PageContent(
        title = "Enter Your\nOrbit",
        description = "In a noisy world, focus is your superpower. Block distractions with proven Pomodoro techniques, work in timed sessions, your way.",
        highlights = listOf(
            FeatureHighlight(Icons.Default.Settings, "Customizable sessions"),
            FeatureHighlight(Icons.Default.Timer, "Set your own focus & break times"),
            FeatureHighlight(Icons.Default.Repeat, "Multiple sets for deep work")
        ),
        imageRes = R.drawable.chaotic_good
    ),

    PageContent(
        title = "Find Your\nFlow",
        description = "Create your perfect focus environment with complete control over your sessions and ambience.",
        highlights = listOf(
            FeatureHighlight(Icons.Default.MusicNote, "6 ambient sounds for focus"),
            FeatureHighlight(Icons.Default.Palette, "8 beautiful color themes"),
            FeatureHighlight(Icons.Default.DarkMode, "Light, dark & AMOLED modes")
        ),
        imageRes = R.drawable.growth
    ),

    PageContent(
        title = "Watch It\nGrow",
        description = "Every focused minute counts. Track your sessions, build consistency, and stay on top of your goals with gentle reminders.",
        highlights = listOf(
            FeatureHighlight(Icons.Default.NotificationsActive, "Gentle daily reminders"),
            FeatureHighlight(Icons.AutoMirrored.Filled.TrendingUp, "Track your focus progress"),
            FeatureHighlight(Icons.Default.BarChart, "Visualize your weekly rhythm")
        ),
        imageRes = R.drawable.jumping
    )
)
