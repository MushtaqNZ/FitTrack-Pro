package com.example.fittrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Exercise entity representing individual exercises
 * Contains exercise information for workout library
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val muscleGroup: String, // "chest", "back", "legs", "shoulders", "arms", "core"
    val equipmentNeeded: String // "none", "dumbbells", "barbell", "machine", "bodyweight"
) 