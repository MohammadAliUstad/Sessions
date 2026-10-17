package com.yugentech.sessions.templates.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.templates.model.Template
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * The user's timer templates in Firestore, at users/{uid}/templates/{name}. Templates are unique
 * by name on the device, so the (encoded) name is the document id: saving one again overwrites
 * it, and deleting it needs only the name.
 *
 * Writes aren't awaited: Firestore applies them to its local cache at once and queues them for
 * the server, so a template saved offline reaches the cloud once the phone is back online.
 */
class TemplateService(
    private val firestore: FirebaseFirestore
) {

    private fun templatesCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("templates")

    /** Document ids can't contain "/" (and a few names are reserved), so the name is encoded. */
    private fun documentId(name: String): String = URLEncoder.encode(name, StandardCharsets.UTF_8.name())

    fun upload(userId: String, template: Template) {
        templatesCollection(userId)
            .document(documentId(template.name))
            .set(template.toMap())
            .addOnSuccessListener { Timber.d("Template synced to cloud: ${template.name}") }
            .addOnFailureListener { Timber.e(it, "Failed to sync template: ${template.name}") }
    }

    fun delete(userId: String, name: String) {
        templatesCollection(userId)
            .document(documentId(name))
            .delete()
            .addOnSuccessListener { Timber.d("Template deleted from cloud: $name") }
            .addOnFailureListener { Timber.e(it, "Failed to delete template from cloud: $name") }
    }

    /** Every template the user has in the cloud, including writes still queued on this device. */
    suspend fun fetchAll(userId: String): List<Template> {
        val snapshot = templatesCollection(userId).get().await()
        return snapshot.documents.mapNotNull { it.toTemplate() }
    }

    private fun Template.toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "focusDuration" to focusDuration,
        "shortBreakDuration" to shortBreakDuration,
        "longBreakDuration" to longBreakDuration,
        "targetSets" to targetSets,
        "setsPerLongBreak" to setsPerLongBreak,
        "longBreakEnabled" to longBreakEnabled,
        "activeBackgroundSoundId" to activeBackgroundSoundId,
        "isAmbientEnabled" to isAmbientEnabled,
        "updatedAt" to FieldValue.serverTimestamp()
    )

    /** Null for a document missing its name or durations, so one bad entry can't break the rest. */
    private fun DocumentSnapshot.toTemplate(): Template? {
        val name = getString("name")?.takeIf { it.isNotBlank() } ?: return null
        return Template(
            name = name,
            focusDuration = getLong("focusDuration")?.toInt() ?: return null,
            shortBreakDuration = getLong("shortBreakDuration")?.toInt() ?: return null,
            longBreakDuration = getLong("longBreakDuration")?.toInt() ?: return null,
            targetSets = getLong("targetSets")?.toInt() ?: return null,
            setsPerLongBreak = getLong("setsPerLongBreak")?.toInt() ?: return null,
            longBreakEnabled = getBoolean("longBreakEnabled") ?: true,
            activeBackgroundSoundId = getString("activeBackgroundSoundId"),
            isAmbientEnabled = getBoolean("isAmbientEnabled") ?: false
        )
    }
}
