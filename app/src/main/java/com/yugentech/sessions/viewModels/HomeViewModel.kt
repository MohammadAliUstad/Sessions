package com.yugentech.sessions.viewModels

import com.yugentech.sessions.BuildConfig
import com.yugentech.sessions.R
import com.yugentech.sessions.localization.AppStrings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.sessions.model.Session
import com.yugentech.sessions.sessions.repository.SessionsRepository
import com.yugentech.sessions.templates.repository.TemplateRepository
import com.yugentech.sessions.user.datastore.UserDataStore
import com.yugentech.sessions.user.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

data class HomeDataState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val shouldShowReviewDialog: Boolean = false
)

class HomeViewModel(
    private val sessionsRepository: SessionsRepository,
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore,
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val _dataState = MutableStateFlow(HomeDataState())
    val dataState: StateFlow<HomeDataState> = _dataState.asStateFlow()

    private var currentUserId: String? = null

    fun initUserData(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId

        viewModelScope.launch {
            fetchUserOnce(userId)
            fetchSessionsOnce()

            syncPendingSessions(userId)
        }
        // Templates: restore the user's from the cloud (a fresh install) and upload any this
        // device has that the cloud doesn't.
        viewModelScope.launch { templateRepository.syncTemplates() }
    }

    fun triggerReviewPrompt() {
        viewModelScope.launch {
            try {
                val lastPrompt = userDataStore.lastReviewPromptTime.first()
                val currentTime = System.currentTimeMillis()
                val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L

                if (currentTime - lastPrompt >= oneWeekInMillis) {
                    // Ensure user has some sessions before asking
                    val sessions = sessionsRepository.getSessionsFlow().first()
                    if (sessions.size >= 3) {
                        Timber.i("Showing Review Dialog")
                        _dataState.update { it.copy(shouldShowReviewDialog = true) }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "triggerReviewPrompt error")
            }
        }
    }

    fun onReviewDialogDismissed() {
        viewModelScope.launch {
            userDataStore.updateLastReviewPromptTime(System.currentTimeMillis())
            _dataState.update { it.copy(shouldShowReviewDialog = false) }
        }
    }

    fun onReviewPromptShown() {
        viewModelScope.launch {
            userDataStore.updateLastReviewPromptTime(System.currentTimeMillis())
            _dataState.update { it.copy(shouldShowReviewDialog = false) }
        }
    }

    suspend fun fetchSessionsOnce() {
        _dataState.value = _dataState.value.copy(isLoading = true)
        try {
            sessionsRepository.fetchSessionsOnce()
            Timber.i("Sessions fetched successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error fetching sessions")
            _dataState.value = _dataState.value.copy(errorMessage = AppStrings.get(R.string.home_history_error))
        } finally {
            _dataState.value = _dataState.value.copy(isLoading = false)
        }
    }

    suspend fun fetchUserOnce(userId: String) {
        try {
            userRepository.fetchUserOnce(userId)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user profile")
        }
    }

    private var dummyDataInjected = false

    /**
     * Fills the history with a year of made-up sessions (300 of them) for screenshots and for
     * trying out Insights. Debug builds only, and once per launch so that coming back to Home
     * doesn't add another batch. They save like real sessions, so a signed-in account syncs
     * them to the cloud.
     */
    fun injectDummyData() {
        if (!BuildConfig.DEBUG || dummyDataInjected) return
        dummyDataInjected = true

        viewModelScope.launch {
            // Weighted tasks — Side Hustle & Work dominate
            val weightedTasks = listOf(
                "Work" to 25,
                "Side Hustle" to 22,
                "Research" to 18,
                "Project" to 15,
                "Resume Polish" to 8,
                "System Design" to 7,
                "Reading" to 3,
                "Meditation" to 2,
            )
            val taskPool = weightedTasks.flatMap { (task, weight) -> List(weight) { task } }

            // Weighted weekdays — Mon–Thu favoured, weekends light
            val weightedDays = listOf(
                Calendar.MONDAY to 20,
                Calendar.TUESDAY to 20,
                Calendar.WEDNESDAY to 18,
                Calendar.THURSDAY to 18,
                Calendar.FRIDAY to 12,
                Calendar.SATURDAY to 7,
                Calendar.SUNDAY to 5,
            )
            val dayPool = weightedDays.flatMap { (day, weight) -> List(weight) { day } }

            repeat(300) {
                val randomDaysAgo = Random.nextInt(0, 365)
                val targetWeekday = dayPool.random()

                val calendar = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -randomDaysAgo)
                    // Nudge to the nearest target weekday (within ±3 days)
                    val currentDay = get(Calendar.DAY_OF_WEEK)
                    var diff = targetWeekday - currentDay
                    if (diff > 3) diff -= 7
                    if (diff < -3) diff += 7
                    add(Calendar.DAY_OF_YEAR, diff)

                    // Bias session times: most work happens 9am–10pm
                    val hour = when (Random.nextInt(100)) {
                        in 0..60 -> Random.nextInt(9, 22)   // 60% → productive hours
                        in 61..80 -> Random.nextInt(22, 24) // 20% → late night
                        else -> Random.nextInt(6, 9)         // 20% → early morning
                    }
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, Random.nextInt(0, 60))
                }
                // The weekday nudge can push the last few days past today; skip those
                if (calendar.timeInMillis > System.currentTimeMillis()) return@repeat

                val dummySession = Session(
                    sessionId = UUID.randomUUID().toString(),
                    duration = Random.nextInt(15, 120) * 60,
                    timestamp = calendar.timeInMillis,
                    sessionTask = taskPool.random()
                )

                sessionsRepository.saveSession(dummySession)
            }

            // An unbroken run over the last 10 days, so the streak and its milestones have
            // something to show (the random spread above rarely lines up days in a row)
            repeat(10) { daysAgo ->
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -daysAgo)
                    set(Calendar.HOUR_OF_DAY, Random.nextInt(6, 10))
                    set(Calendar.MINUTE, Random.nextInt(0, 60))
                }
                if (calendar.timeInMillis > System.currentTimeMillis()) return@repeat
                sessionsRepository.saveSession(
                    Session(
                        sessionId = UUID.randomUUID().toString(),
                        duration = Random.nextInt(15, 120) * 60,
                        timestamp = calendar.timeInMillis,
                        sessionTask = taskPool.random()
                    )
                )
            }

            Timber.d("Yearly dummy data injection complete!")
        }
    }

    fun syncPendingSessions(userId: String? = currentUserId) {
        userId ?: return
        viewModelScope.launch {
            try {
                sessionsRepository.syncSessions()
                Timber.i("Offline sessions synced")
            } catch (e: Exception) {
                Timber.e(e, "Sync failed")
            }
        }
    }
}