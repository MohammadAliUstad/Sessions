package com.yugentech.sessions.auth.guest

import android.content.ContentResolver
import android.net.Uri
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.daos.TemplateDao
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.room.entities.TemplateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

/**
 * Saves a guest's sessions, templates and profile to one JSON file they choose, and reads such a
 * file back. A guest has no account to sign in to again, so this is how they carry their progress
 * to another phone or through a reinstall. A signed-in user can import one too, which is how a
 * guest's progress comes into a new account (it then syncs to Firestore like the rest).
 *
 * Importing merges, never replaces: sessions already here (by ID) and templates with a name already
 * here are skipped, so importing the same file twice changes nothing. The file may have been edited
 * or damaged, so every entry is checked and bad ones are dropped rather than failing the whole file.
 */
class GuestBackupManager(
    private val contentResolver: ContentResolver,
    private val sessionsDao: SessionsDao,
    private val templateDao: TemplateDao,
    private val userDao: UserDao
) {

    /** What an import added. */
    data class ImportSummary(val sessionsAdded: Int, val templatesAdded: Int)

    /** Writes the guest's data to [uri]. Returns false when the file couldn't be written. */
    suspend fun export(userId: String, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject()
                .put(KEY_APP, APP_ID)
                .put(KEY_VERSION, FORMAT_VERSION)
                .put(KEY_EXPORTED_AT, System.currentTimeMillis())

            userDao.getUser(userId)?.let { user ->
                json.put(KEY_PROFILE, JSONObject().apply {
                    user.name?.let { put("name", it) }
                    user.avatarId?.let { put("avatarId", it) }
                })
            }

            json.put(KEY_SESSIONS, JSONArray().apply {
                sessionsDao.getSessionsOnce(userId).forEach { s ->
                    put(JSONObject()
                        .put("id", s.sessionId)
                        .put("duration", s.duration)
                        .put("timestamp", s.timestamp)
                        .put("task", s.sessionTask))
                }
            })

            json.put(KEY_TEMPLATES, JSONArray().apply {
                templateDao.getAllOnce().forEach { t ->
                    put(JSONObject()
                        .put("name", t.name)
                        .put("focusDuration", t.focusDuration)
                        .put("shortBreakDuration", t.shortBreakDuration)
                        .put("longBreakDuration", t.longBreakDuration)
                        .put("targetSets", t.targetSets)
                        .put("setsPerLongBreak", t.setsPerLongBreak)
                        .put("longBreakEnabled", t.longBreakEnabled)
                        .put("isAmbientEnabled", t.isAmbientEnabled)
                        .apply { t.activeBackgroundSoundId?.let { put("activeBackgroundSoundId", it) } })
                }
            })

            // "wt" truncates an existing file; a few providers only know "w"
            val stream = runCatching { contentResolver.openOutputStream(uri, "wt") }.getOrNull()
                ?: contentResolver.openOutputStream(uri, "w")
                ?: return@withContext false
            stream.bufferedWriter(Charsets.UTF_8).use { it.write(json.toString(2)) }
            Timber.i("Guest backup exported")
            true
        } catch (e: Exception) {
            Timber.e(e, "Guest backup export failed")
            false
        }
    }

    /**
     * Adds what's in the backup at [uri] to the guest's data. Returns null when the file isn't a
     * Sessions backup (or is too damaged to read at all). Like all guest data, what's added stays
     * on this phone until the guest makes an account.
     */
    suspend fun import(userId: String, uri: Uri): ImportSummary? = withContext(Dispatchers.IO) {
        try {
            val text = contentResolver.openInputStream(uri)?.use { input ->
                val bytes = input.readAtMost(MAX_FILE_BYTES)
                bytes?.toString(Charsets.UTF_8)
            } ?: return@withContext null

            val json = JSONObject(text)
            if (json.optString(KEY_APP) != APP_ID) return@withContext null
            val version = json.optInt(KEY_VERSION, -1)
            if (version !in 1..FORMAT_VERSION) return@withContext null

            val sessionsAdded = importSessions(userId, json.optJSONArray(KEY_SESSIONS))
            val templatesAdded = importTemplates(json.optJSONArray(KEY_TEMPLATES))
            json.optJSONObject(KEY_PROFILE)?.let { importProfile(userId, it) }

            Timber.i("Guest backup imported: $sessionsAdded sessions, $templatesAdded templates")
            ImportSummary(sessionsAdded, templatesAdded)
        } catch (e: Exception) {
            Timber.e(e, "Guest backup import failed")
            null
        }
    }

    private suspend fun importSessions(userId: String, array: JSONArray?): Int {
        if (array == null) return 0
        val now = System.currentTimeMillis()
        val parsed = (0 until minOf(array.length(), MAX_SESSIONS)).mapNotNull { i ->
            val o = array.optJSONObject(i) ?: return@mapNotNull null
            val id = o.optString("id").trim()
            val duration = o.optInt("duration", -1)
            val timestamp = o.optLong("timestamp", -1)
            val task = o.optString("task").trim().take(MAX_TEXT)
            when {
                id.isEmpty() || id.length > MAX_TEXT -> null
                duration !in 1..MAX_DURATION -> null
                timestamp <= 0 || timestamp > now + DAY_MS -> null
                else -> SessionsEntity(
                    sessionId = id,
                    userId = userId,
                    duration = duration,
                    timestamp = timestamp,
                    pendingSync = true,
                    sessionTask = task.ifEmpty { DEFAULT_TASK }
                )
            }
        }.distinctBy { it.sessionId }

        // Skip sessions already here, including ones another user on this phone owns
        val existing = parsed.map { it.sessionId }.chunked(QUERY_CHUNK)
            .flatMapTo(HashSet()) { sessionsDao.getExistingIds(it) }
        val added = parsed.filter { it.sessionId !in existing }
        if (added.isNotEmpty()) sessionsDao.saveSessions(added)
        return added.size
    }

    private suspend fun importTemplates(array: JSONArray?): Int {
        if (array == null) return 0
        val names = templateDao.getAllOnce().mapTo(HashSet()) { it.name }
        var added = 0
        for (i in 0 until minOf(array.length(), MAX_TEMPLATES)) {
            val o = array.optJSONObject(i) ?: continue
            val name = o.optString("name").trim()
            if (name.isEmpty() || name.length > MAX_TEXT || name in names) continue

            val focus = o.optInt("focusDuration", -1)
            val shortBreak = o.optInt("shortBreakDuration", -1)
            val longBreak = o.optInt("longBreakDuration", -1)
            val targetSets = o.optInt("targetSets", -1)
            val setsPerLongBreak = o.optInt("setsPerLongBreak", -1)
            if (focus !in 1..MAX_DURATION || shortBreak !in 1..MAX_DURATION || longBreak !in 1..MAX_DURATION) continue
            if (targetSets !in 1..MAX_SETS || setsPerLongBreak !in 1..MAX_SETS) continue

            templateDao.upsert(
                TemplateEntity(
                    name = name,
                    focusDuration = focus,
                    shortBreakDuration = shortBreak,
                    longBreakDuration = longBreak,
                    targetSets = targetSets,
                    setsPerLongBreak = setsPerLongBreak,
                    longBreakEnabled = o.optBoolean("longBreakEnabled", true),
                    activeBackgroundSoundId = o.optString("activeBackgroundSoundId")
                        .takeIf { it.isNotBlank() && it.length <= MAX_TEXT },
                    isAmbientEnabled = o.optBoolean("isAmbientEnabled", false)
                )
            )
            names += name
            added++
        }
        return added
    }

    /** Takes the backup's name and avatar only if the guest hasn't named themselves here yet. */
    private suspend fun importProfile(userId: String, profile: JSONObject) {
        val user = userDao.getUser(userId) ?: return
        if (user.name != null) return
        val name = profile.optString("name").trim().take(MAX_TEXT).ifEmpty { null } ?: return
        val avatarId = if (profile.has("avatarId")) profile.optInt("avatarId") else user.avatarId
        userDao.saveUser(user.copy(name = name, avatarId = avatarId))
    }

    /** Reads at most [limit] bytes; null if the stream holds more, so a huge file isn't loaded. */
    private fun java.io.InputStream.readAtMost(limit: Int): ByteArray? {
        val out = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(8 * 1024)
        while (true) {
            val read = read(buffer)
            if (read < 0) break
            if (out.size() + read > limit) return null
            out.write(buffer, 0, read)
        }
        return out.toByteArray()
    }

    private companion object {
        const val APP_ID = "com.yugentech.sessions"
        const val FORMAT_VERSION = 1

        const val KEY_APP = "app"
        const val KEY_VERSION = "version"
        const val KEY_EXPORTED_AT = "exportedAt"
        const val KEY_PROFILE = "profile"
        const val KEY_SESSIONS = "sessions"
        const val KEY_TEMPLATES = "templates"

        const val DEFAULT_TASK = "Focus Session"
        const val MAX_FILE_BYTES = 20 * 1024 * 1024
        const val MAX_SESSIONS = 200_000
        const val MAX_TEMPLATES = 500
        const val MAX_TEXT = 200
        const val MAX_DURATION = 24 * 60 * 60
        const val MAX_SETS = 1_000
        const val DAY_MS = 24 * 60 * 60 * 1000L
        const val QUERY_CHUNK = 500
    }
}
