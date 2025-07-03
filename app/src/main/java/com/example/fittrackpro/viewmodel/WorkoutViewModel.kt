package com.example.fittrackpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittrackpro.data.FitTrackDatabase
import com.example.fittrackpro.data.dao.WorkoutSessionWithEntries
import com.example.fittrackpro.data.entity.Exercise
import com.example.fittrackpro.data.entity.WorkoutEntry
import com.example.fittrackpro.data.entity.WorkoutSession
import com.example.fittrackpro.data.entity.WorkoutTemplate
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for workout-related operations
 * Handles workout logging, template management, and session tracking
 */
class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = FitTrackDatabase.getDatabase(application)
    private val workoutDao = database.workoutDao()
    private val exerciseDao = database.exerciseDao()
    
    private val _workoutTemplates = MutableLiveData<List<WorkoutTemplate>>()
    val workoutTemplates: LiveData<List<WorkoutTemplate>> = _workoutTemplates
    
    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercises
    
    private val _recentWorkouts = MutableLiveData<List<WorkoutSessionWithEntries>>()
    val recentWorkouts: LiveData<List<WorkoutSessionWithEntries>> = _recentWorkouts
    
    private val _isWorkoutSaved = MutableLiveData<Boolean>()
    val isWorkoutSaved: LiveData<Boolean> = _isWorkoutSaved
    
    /**
     * Load all workout templates
     */
    fun loadWorkoutTemplates() {
        viewModelScope.launch {
            workoutDao.getAllWorkoutTemplates().collect { templates ->
                _workoutTemplates.value = templates
            }
        }
    }
    
    /**
     * Load workout templates by category
     */
    fun loadWorkoutTemplatesByCategory(category: String) {
        viewModelScope.launch {
            workoutDao.getWorkoutTemplatesByCategory(category).collect { templates ->
                _workoutTemplates.value = templates
            }
        }
    }
    
    /**
     * Load all exercises
     */
    fun loadExercises() {
        viewModelScope.launch {
            exerciseDao.getAllExercises().collect { exerciseList ->
                _exercises.value = exerciseList
            }
        }
    }
    
    /**
     * Load exercises by muscle group
     */
    fun loadExercisesByMuscleGroup(muscleGroup: String) {
        viewModelScope.launch {
            exerciseDao.getExercisesByMuscleGroup(muscleGroup).collect { exerciseList ->
                _exercises.value = exerciseList
            }
        }
    }
    
    /**
     * Load recent workout sessions for a user
     */
    fun loadRecentWorkouts(userId: Long) {
        viewModelScope.launch {
            workoutDao.getRecentWorkoutSessionsWithEntries(userId).collect { workouts ->
                _recentWorkouts.value = workouts
            }
        }
    }
    
    /**
     * Save a new workout session with entries
     */
    fun saveWorkoutSession(
        userId: Long,
        templateId: Long?,
        duration: Int,
        notes: String,
        entries: List<Triple<Long, Int, Float>> // exerciseId, sets, weight
    ) {
        viewModelScope.launch {
            try {
                // Create workout session
                val session = WorkoutSession(
                    userId = userId,
                    templateId = templateId,
                    duration = duration,
                    notes = notes
                )
                
                val sessionId = workoutDao.insertWorkoutSession(session)
                
                // Create workout entries
                entries.forEach { (exerciseId, sets, weight) ->
                    val entry = WorkoutEntry(
                        sessionId = sessionId,
                        exerciseId = exerciseId,
                        sets = sets,
                        reps = 10, // Default reps for MVP
                        weight = weight
                    )
                    workoutDao.insertWorkoutEntry(entry)
                }
                
                _isWorkoutSaved.value = true
                
                // Reload recent workouts
                loadRecentWorkouts(userId)
                
            } catch (e: Exception) {
                _isWorkoutSaved.value = false
            }
        }
    }
    
    /**
     * Delete a workout session
     */
    fun deleteWorkoutSession(session: WorkoutSession) {
        viewModelScope.launch {
            workoutDao.deleteWorkoutSession(session)
            // Reload recent workouts
            session.userId.let { loadRecentWorkouts(it) }
        }
    }
    
    /**
     * Get workout statistics for progress tracking
     */
    fun getWorkoutStats(userId: Long): LiveData<WorkoutStats> {
        val stats = MutableLiveData<WorkoutStats>()
        
        viewModelScope.launch {
            val recentWorkouts = workoutDao.getRecentWorkoutSessionsWithEntries(userId)
            recentWorkouts.collect { workoutList ->
                val totalWorkouts = workoutList.size
                val totalDuration = workoutList.sumOf { it.session.duration }
                val averageDuration = if (totalWorkouts > 0) totalDuration / totalWorkouts else 0
                
                val statsData = WorkoutStats(
                    totalWorkouts = totalWorkouts,
                    totalDuration = totalDuration,
                    averageDuration = averageDuration,
                    lastWorkoutDate = workoutList.firstOrNull()?.session?.date
                )
                
                stats.value = statsData
            }
        }
        
        return stats
    }
}

/**
 * Data class for workout statistics
 */
data class WorkoutStats(
    val totalWorkouts: Int,
    val totalDuration: Int,
    val averageDuration: Int,
    val lastWorkoutDate: Date?
) 