package com.yugentech.sessions.models

data class User(
    val userId: String = "",
    val name: String = "",
    val totalTimeStudied: Long = 0,
    val isStudying: Boolean = false
) {
    companion object {
        fun fromMap(userId: String, data: Map<String, Any>): User {
            return User(
                userId = userId,
                name = data["name"] as? String ?: "No Name",
                totalTimeStudied = (data["totalTimeStudied"] as? Long) ?: 0L,
                isStudying = (data["isStudying"] as? Boolean) == true
            )
        }
    }
}