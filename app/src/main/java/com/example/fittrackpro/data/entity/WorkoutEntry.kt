package com.example.fittrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * WorkoutEntry entity representing individual exercises within a workout session
 * Tracks sets, reps, and weight for each exercise
 */
@Entity(
    tableName = "workout_entries",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val sets: Int,
    val reps: Int,
    val weight: Float // in pounds
) 