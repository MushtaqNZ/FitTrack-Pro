package com.example.fittrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WorkoutTemplate entity representing pre-built workout plans
 * Provides structured workout options for users
 */
@Entity(tableName = "workout_templates")
data class WorkoutTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val category: String, // "strength", "cardio", "mixed"
    val difficultyLevel: String // "beginner", "intermediate", "advanced"
) 