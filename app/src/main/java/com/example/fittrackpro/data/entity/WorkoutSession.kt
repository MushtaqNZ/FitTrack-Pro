package com.example.fittrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

/**
 * WorkoutSession entity representing individual workout sessions
 * Links users to their workout templates and tracks session details
 */
@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutTemplate::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val templateId: Long?,
    val date: Date = Date(),
    val duration: Int, // in minutes
    val notes: String = ""
) 