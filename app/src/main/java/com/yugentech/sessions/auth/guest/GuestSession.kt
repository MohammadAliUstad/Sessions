package com.yugentech.sessions.auth.guest

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Guest mode's "account": a random ID kept only on this phone. Nothing is created in Firebase;
 * the ID only keys the guest's sessions, templates and profile in the local database. Kept in
 * SharedPreferences so it can be read straight away, before anything else loads.
 */
class GuestSession(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val _guestId = MutableStateFlow(prefs.getString(KEY_GUEST_ID, null))

    /** The current guest's ID, or null when no one is in guest mode. */
    val guestId: StateFlow<String?> = _guestId.asStateFlow()

    /** Starts guest mode with a new ID (or keeps the current one) and returns it. */
    fun start(): String = adopt(_guestId.value ?: "guest-${UUID.randomUUID()}")

    /** Uses [id] as the guest ID, e.g. an anonymous Firebase ID from an earlier build. */
    fun adopt(id: String): String {
        prefs.edit(commit = true) { putString(KEY_GUEST_ID, id) }
        _guestId.value = id
        return id
    }

    /** Ends guest mode; the caller removes the guest's data first. */
    fun end() {
        prefs.edit(commit = true) { remove(KEY_GUEST_ID) }
        _guestId.value = null
    }

    private companion object {
        const val PREFS = "guest_session"
        const val KEY_GUEST_ID = "guest_id"
    }
}
