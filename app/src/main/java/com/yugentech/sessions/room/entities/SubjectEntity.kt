package com.yugentech.sessions.room.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yugentech.sessions.models.Subject

@Keep
@Entity(
    tableName = "subjects",
    indices = [Index(value = ["userId"])]
)
data class SubjectEntity(
    @PrimaryKey
    val subjectId: String,
    val userId: String,
    val name: String
) {
    fun toSubject(): Subject {
        return Subject(
            subjectId = subjectId,
            userId = userId,
            name = name
        )
    }

    companion object {
        fun fromSubject(subject: Subject): SubjectEntity {
            return SubjectEntity(
                subjectId = subject.subjectId,
                userId = subject.userId,
                name = subject.name
            )
        }
    }
}