package com.yugentech.sessions.models

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class Subject(
    val subjectId: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String
) {
    // Serialize to Map for Firestore
    fun toMap(): Map<String, Any?> = mapOf(
        "subjectId" to subjectId,
        "userId" to userId,
        "name" to name
    )
}