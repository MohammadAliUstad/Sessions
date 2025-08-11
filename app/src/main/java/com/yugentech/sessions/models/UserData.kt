package com.yugentech.sessions.models

data class UserData(
    val userId: String = "",
    val name: String? = null,
    val email: String? = null,
    val avatarId: Int? = 0,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,
        "avatarId" to avatarId
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): UserData? {
            val userId = map["userId"] as? String
            val name = map["name"] as? String
            val email = map["email"] as? String
            val avatarId = map["avatarId"] as? Int

            return if (userId != null) {
                UserData(
                    userId = userId,
                    name = name,
                    email = email,
                    avatarId = avatarId
                )
            } else null
        }
    }
}