package com.example.fittrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * User entity representing app users
 * Stores basic user information and preferences
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val fitnessLevel: String, // "beginner", "intermediate", "advanced"
    val preferredWorkoutType: String, // "strength", "cardio", "mixed"
    val createdDate: Date = Date()
) 