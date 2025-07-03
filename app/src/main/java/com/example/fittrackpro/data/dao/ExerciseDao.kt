package com.example.fittrackpro.data.dao

import androidx.room.*
import com.example.fittrackpro.data.entity.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Exercise entity
 * Provides database operations for exercise library management
 */
@Dao
interface ExerciseDao {
    
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscleGroup")
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE equipmentNeeded = :equipment")
    fun getExercisesByEquipment(equipment: String): Flow<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchExercises(searchQuery: String): Flow<List<Exercise>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)
    
    @Update
    suspend fun updateExercise(exercise: Exercise)
    
    @Delete
    suspend fun deleteExercise(exercise: Exercise)
    
    @Query("SELECT DISTINCT muscleGroup FROM exercises ORDER BY muscleGroup")
    fun getMuscleGroups(): Flow<List<String>>
    
    @Query("SELECT DISTINCT equipmentNeeded FROM exercises ORDER BY equipmentNeeded")
    fun getEquipmentTypes(): Flow<List<String>>
} 