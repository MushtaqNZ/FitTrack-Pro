package com.example.fittrackpro.data.dao

import androidx.room.*
import com.example.fittrackpro.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for workout-related entities
 * Provides database operations for workout templates, sessions, and entries
 */
@Dao
interface WorkoutDao {
    
    // Workout Templates
    @Query("SELECT * FROM workout_templates")
    fun getAllWorkoutTemplates(): Flow<List<WorkoutTemplate>>
    
    @Query("SELECT * FROM workout_templates WHERE category = :category")
    fun getWorkoutTemplatesByCategory(category: String): Flow<List<WorkoutTemplate>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutTemplate(template: WorkoutTemplate): Long
    
    // Workout Sessions
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getWorkoutSessionsByUser(userId: Long): Flow<List<WorkoutSession>>
    
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId AND date >= :startDate ORDER BY date DESC")
    fun getWorkoutSessionsByUserAndDate(userId: Long, startDate: Date): Flow<List<WorkoutSession>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSession): Long
    
    @Delete
    suspend fun deleteWorkoutSession(session: WorkoutSession)
    
    // Workout Entries
    @Query("SELECT * FROM workout_entries WHERE sessionId = :sessionId")
    suspend fun getWorkoutEntriesBySession(sessionId: Long): List<WorkoutEntry>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutEntry(entry: WorkoutEntry): Long
    
    @Delete
    suspend fun deleteWorkoutEntry(entry: WorkoutEntry)
    
    // Combined queries for workout data
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC LIMIT 30")
    fun getRecentWorkoutSessionsWithEntries(userId: Long): Flow<List<WorkoutSessionWithEntries>>
}

/**
 * Data class for workout sessions with their entries
 */
data class WorkoutSessionWithEntries(
    @Embedded val session: WorkoutSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val entries: List<WorkoutEntryWithExercise>
)

/**
 * Data class for workout entries with exercise details
 */
data class WorkoutEntryWithExercise(
    @Embedded val entry: WorkoutEntry,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: Exercise
) 