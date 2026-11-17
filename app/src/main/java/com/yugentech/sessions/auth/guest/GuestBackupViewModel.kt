package com.yugentech.sessions.auth.guest

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.auth.repository.AuthRepository
import com.yugentech.sessions.sessions.repository.SessionsRepository
import com.yugentech.sessions.templates.repository.TemplateRepository
import com.yugentech.sessions.user.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The Backup rows in Settings. Anyone can export and import. A guest's import is how they carry
 * their progress to another phone; a signed-in user's import is how a guest's saved file comes into
 * an account (what's added then syncs to it), and a signed-in user's export is how an account's
 * progress goes the other way, into guest mode.
 */
class GuestBackupViewModel(
    private val authRepository: AuthRepository,
    private val backupManager: GuestBackupManager,
    private val sessionsRepository: SessionsRepository,
    private val templateRepository: TemplateRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    sealed interface Result {
        data object Exported : Result
        data object ExportFailed : Result
        data class Imported(val sessions: Int, val templates: Int) : Result
        data object ImportFailed : Result
    }

    data class State(val busy: Boolean = false, val result: Result? = null)

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun export(uri: Uri) = launchBackup { userId ->
        // Signed in: the file is written from this phone's copy, so make sure the account's
        // sessions have been brought down first (a no-op once they have; offline it moves on).
        if (!authRepository.isGuest) sessionsRepository.fetchSessionsOnce()
        if (backupManager.export(userId, uri)) Result.Exported else Result.ExportFailed
    }

    fun import(uri: Uri) = launchBackup { userId ->
        // Signed in: bring the account's templates down first, so the import's name check sees
        // them. Otherwise, on a fresh install, a backup template sharing a cloud template's name
        // is added here and the two never reconcile. Offline this does nothing and moves on.
        if (!authRepository.isGuest) templateRepository.syncTemplates()
        val summary = backupManager.import(userId, uri)
        if (summary == null) {
            Result.ImportFailed
        } else {
            // Signed in: upload what was added. For a guest these do nothing (their data stays
            // on the phone). Offline they wait for the network, so they don't hold up the result.
            if (!authRepository.isGuest && (summary.sessionsAdded > 0 || summary.templatesAdded > 0)) {
                viewModelScope.launch { syncImported(userId) }
            }
            Result.Imported(summary.sessionsAdded, summary.templatesAdded)
        }
    }

    private suspend fun syncImported(userId: String) {
        sessionsRepository.syncSessions()
        templateRepository.syncTemplates()
        // The backup's name is used only when the profile had none, so this is usually unchanged
        userRepository.getUser(userId)?.let { userRepository.syncUser(it) }
    }

    fun dismissResult() = _state.update { it.copy(result = null) }

    private fun launchBackup(block: suspend (userId: String) -> Result) {
        val userId = authRepository.currentUser ?: return
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            val result = block(userId)
            _state.update { State(busy = false, result = result) }
        }
    }
}
