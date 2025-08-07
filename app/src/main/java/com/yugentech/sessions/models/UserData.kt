package com.yugentech.sessions.models

data class UserData(
    val userId: String = "",
    val name: String? = null,
    val email: String? = null,
    val avatarId: Int = 1,
    val totalTimeStudied: Long = 0L,
    val lastSyncTimestamp: Long = 0L,
    val pendingSync: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to userId,
        "name" to name,
        "email" to email,
        "avatarId" to avatarId,
        "totalTimeStudied" to totalTimeStudied,
        "lastSyncTimestamp" to lastSyncTimestamp
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): UserData? {
            val uid = map["uid"] as? String
            val name = map["name"] as? String
            val email = map["email"] as? String
            val avatarId = (map["avatarId"] as? Long)?.toInt() ?: 1
            val totalTimeStudied = map["totalTimeStudied"] as? Long ?: 0L
            val lastSyncTimestamp = map["lastSyncTimestamp"] as? Long ?: 0L

            return if (uid != null) {
                UserData(
                    userId = uid,
                    name = name,
                    email = email,
                    avatarId = avatarId,
                    totalTimeStudied = totalTimeStudied,
                    lastSyncTimestamp = lastSyncTimestamp,
                    pendingSync = false
                )
            } else null
        }
    }
}