package com.yugentech.sessions.announcements.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.announcements.model.Announcement
import com.yugentech.sessions.announcements.repository.AnnouncementRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class AnnouncementState(
    /** The announcement currently being presented (dialog or message), or null. */
    val current: Announcement? = null,
    /** Remaining announcements queued for presentation after current is resolved. */
    val queue: List<Announcement> = emptyList(),
    val isInitialized: Boolean = false
)

// The pause between one announcement closing and the next one opening.
private const val BetweenAnnouncementsMillis = 700L

class AnnouncementViewModel(
    private val repository: AnnouncementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnnouncementState())
    val state: StateFlow<AnnouncementState> = _state.asStateFlow()

    fun checkForAnnouncements() {
        if (_state.value.isInitialized) return

        viewModelScope.launch {
            try {
                val pending = repository.getPendingAnnouncements()
                Timber.d("Pending announcements count: ${pending.size}")

                if (pending.isNotEmpty()) {
                    val first = pending.first()
                    val rest = pending.drop(1)
                    // Mark the first one as seen immediately so recreation / configuration
                    // changes don't re-trigger it.
                    repository.markSeen(first.id)
                    _state.value = AnnouncementState(
                        current = first,
                        queue = rest,
                        isInitialized = true
                    )
                } else {
                    _state.value = AnnouncementState(isInitialized = true)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error checking for announcements")
                _state.value = AnnouncementState(isInitialized = true)
            }
        }
    }

    /**
     * Resolves the current announcement and advances to the next in queue (if any).
     */
    fun dismissCurrent() {
        val currentQueue = _state.value.queue
        if (currentQueue.isNotEmpty()) {
            val next = currentQueue.first()
            val remaining = currentQueue.drop(1)
            // Close this one first and leave a short pause before the next, so they read as
            // two separate announcements rather than one swapping its contents
            _state.update { it.copy(current = null, queue = remaining) }
            viewModelScope.launch {
                repository.markSeen(next.id)
                delay(BetweenAnnouncementsMillis)
                _state.update { it.copy(current = next) }
            }
        } else {
            _state.update {
                it.copy(current = null, queue = emptyList())
            }
        }
    }
}
