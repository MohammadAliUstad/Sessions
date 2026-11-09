package com.yugentech.sessions.ui.dash.util

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.yugentech.sessions.navigation.screen.AppScreen

/*
 * Material 3 motion for screen changes, matching Quill's NavTransitions.
 *
 * Shared axis X, for opening a screen and going back: both screens move 30 dp along the
 * x axis while the old one fades out quickly and the new one fades in after it.
 * Fade through, for switching section (sign-in, onboarding, the app): no movement, the old
 * section fades out and the new one fades in while scaling up from 92%.
 * Shared axis Y, for onboarding -> sign-in / sign-up (the next step of one journey, over the
 * same background) and back: the content moves 30 dp up into sign-in, down out of it.
 */

private const val DURATION_MS = 300

/** Sign In <-> Sign Up: slower, so the move between the two auth screens reads calmly. */
private const val AUTH_DURATION_MS = 750

/** Finishing onboarding into the app: slower, so the big hand-off doesn't snap. */
private const val ONBOARDING_DONE_MS = 600

/** Sign-in / sign-up <-> onboarding, in shared axis Y. */
private const val AUTH_ONBOARDING_MS = 500

/** The share of the duration in which the outgoing screen fades out (Material: 90 of 300 ms). */
private const val FADE_OUT_SHARE = 0.3f

/** Material 3's emphasized easing (motion.easing.emphasized). */
private val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

/** The app's one [NavTransitions], for the nav host and for screens that animate their own content. */
@Composable
fun rememberNavTransitions(): NavTransitions {
    val slideDistancePx = with(LocalDensity.current) { 30.dp.roundToPx() }
    return remember(slideDistancePx) { NavTransitions(slideDistancePx) }
}

/** [slideDistancePx]: how far screens move in shared axis X (30 dp; the transitions work in pixels). */
class NavTransitions(private val slideDistancePx: Int) {

    /**
     * Switching tabs, with the same shared axis X as opening a screen: [forward] when the new
     * tab is to the right of the old one, so they move the way the tab bar reads.
     */
    fun tabSwitch(forward: Boolean): ContentTransform =
        sharedAxisIn(forward, DURATION_MS) togetherWith sharedAxisOut(forward, DURATION_MS)

    /** Swapping what one screen shows for something unrelated: fade through. */
    fun fadeThrough(): ContentTransform =
        fadeThroughIn(DURATION_MS) togetherWith fadeThroughOut(DURATION_MS)

    fun enter(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition =
        scope.authOnboardingDirection()?.let { sharedAxisYIn(it) }
            ?: if (scope.switchesSection() && !scope.replayHop()) fadeThroughIn(scope.duration())
            else sharedAxisIn(forward = true, scope.duration())

    fun exit(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition =
        scope.authOnboardingDirection()?.let { sharedAxisYOut(it) }
            ?: if (scope.switchesSection() && !scope.replayHop()) fadeThroughOut(scope.duration())
            else sharedAxisOut(forward = true, scope.duration())

    fun popEnter(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition =
        scope.authOnboardingDirection()?.let { sharedAxisYIn(it) }
            ?: if (scope.switchesSection() && !scope.replayHop()) fadeThroughIn(scope.duration())
            // Finishing the replay moves on to Settings, so it reads forward rather than back
            else sharedAxisIn(forward = scope.replayHop(), scope.duration())

    fun popExit(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition =
        scope.authOnboardingDirection()?.let { sharedAxisYOut(it) }
            ?: if (scope.switchesSection() && !scope.replayHop()) fadeThroughOut(scope.duration())
            else sharedAxisOut(forward = scope.replayHop(), scope.duration())

    /** [direction] 1: up into sign-in, from below. -1: back down to onboarding, from above. */
    private fun sharedAxisYIn(direction: Int): EnterTransition =
        slideInVertically(tween(AUTH_ONBOARDING_MS, easing = EmphasizedEasing)) {
            direction * slideDistancePx
        } + fadeIn(fadeInSpec(AUTH_ONBOARDING_MS))

    /** [direction] 1: up and away into sign-in. -1: down and away back to onboarding. */
    private fun sharedAxisYOut(direction: Int): ExitTransition =
        slideOutVertically(tween(AUTH_ONBOARDING_MS, easing = EmphasizedEasing)) {
            -direction * slideDistancePx
        } + fadeOut(fadeOutSpec(AUTH_ONBOARDING_MS))

    /** Forward: comes in from the right. Back: from the left. */
    private fun sharedAxisIn(forward: Boolean, durationMs: Int): EnterTransition =
        slideInHorizontally(tween(durationMs, easing = EmphasizedEasing)) {
            if (forward) slideDistancePx else -slideDistancePx
        } + fadeIn(fadeInSpec(durationMs))

    /** Forward: leaves to the left. Back: to the right. */
    private fun sharedAxisOut(forward: Boolean, durationMs: Int): ExitTransition =
        slideOutHorizontally(tween(durationMs, easing = EmphasizedEasing)) {
            if (forward) -slideDistancePx else slideDistancePx
        } + fadeOut(fadeOutSpec(durationMs))

    private fun fadeThroughIn(durationMs: Int): EnterTransition =
        fadeIn(fadeInSpec(durationMs)) + scaleIn(
            tween(
                durationMs - fadeOutMs(durationMs),
                delayMillis = fadeOutMs(durationMs),
                easing = EmphasizedEasing
            ),
            initialScale = 0.92f
        )

    private fun fadeThroughOut(durationMs: Int): ExitTransition = fadeOut(fadeOutSpec(durationMs))
}

private fun fadeOutMs(durationMs: Int) = (durationMs * FADE_OUT_SHARE).toInt()

private fun fadeInSpec(durationMs: Int) = tween<Float>(
    durationMs - fadeOutMs(durationMs),
    delayMillis = fadeOutMs(durationMs),
    easing = LinearOutSlowInEasing
)

private fun fadeOutSpec(durationMs: Int) = tween<Float>(fadeOutMs(durationMs), easing = FastOutLinearInEasing)

/**
 * Within the auth screens the transition is slower ([AUTH_DURATION_MS]), and so is finishing
 * onboarding into the app ([ONBOARDING_DONE_MS]); everywhere else Material's 300 ms.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.duration(): Int {
    val from = section(initialState)
    val to = section(targetState)
    return when {
        replayHop() -> DURATION_MS
        from == Section.AUTH && to == Section.AUTH -> AUTH_DURATION_MS
        from == Section.ONBOARDING && to == Section.APP -> ONBOARDING_DONE_MS
        else -> DURATION_MS
    }
}

/**
 * 1 from onboarding on to the auth screens (Sessions' next step, so it moves up, as Amatyaa's
 * auth -> onboarding does), -1 back from them to onboarding, null for any other move.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.authOnboardingDirection(): Int? {
    val from = section(initialState)
    val to = section(targetState)
    return when {
        from == Section.ONBOARDING && to == Section.AUTH -> 1
        from == Section.AUTH && to == Section.ONBOARDING -> -1
        else -> null
    }
}

/**
 * Opening the onboarding replay from Settings, or going back from it to the app. It moves like any
 * other screen, with the standard shared axis X at 300 ms (forward both ways), rather than the
 * section fade through and its zoom that first-run onboarding uses on its way into the app.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.replayHop(): Boolean {
    val replay = AppScreen.OnboardingReplay.route
    return (initialState.destination.route == replay && section(targetState) == Section.APP) ||
        (targetState.destination.route == replay && section(initialState) == Section.APP)
}

/** Sign-in, onboarding and the app are separate sections; moving between them fades through. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.switchesSection(): Boolean =
    section(initialState) != section(targetState)

private enum class Section { AUTH, ONBOARDING, APP }

private fun section(entry: NavBackStackEntry): Section = when (entry.destination.route) {
    AppScreen.SignIn.route, AppScreen.SignUp.route -> Section.AUTH
    AppScreen.Onboarding.route, AppScreen.OnboardingReplay.route -> Section.ONBOARDING
    else -> Section.APP
}
