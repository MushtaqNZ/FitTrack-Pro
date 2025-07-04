package com.example.fittrackpro.data.model

import com.example.fittrackpro.data.entity.WorkoutEntry
import com.example.fittrackpro.data.entity.Exercise
import com.example.fittrackpro.data.entity.WorkoutSession

/**
 * Data class for workout entries with exercise details
 */
data class WorkoutEntryWithExercise(
    val entry: WorkoutEntry,
    val exercise: Exercise
)

/**
 * Data class for workout sessions with their entries
 */
data class WorkoutSessionWithEntries(
    val session: WorkoutSession,
    val entries: List<WorkoutEntryWithExercise>
) 