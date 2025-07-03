package com.example.fittrackpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittrackpro.data.FitTrackDatabase
import com.example.fittrackpro.data.dao.WorkoutSessionWithEntries
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * ViewModel for progress tracking and achievement system
 * Handles workout statistics, streaks, and achievement badges
 */
class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = FitTrackDatabase.getDatabase(application)
    private val workoutDao = database.workoutDao()
    
    private val _workoutStats = MutableLiveData<ProgressStats>()
    val workoutStats: LiveData<ProgressStats> = _workoutStats
    
    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> = _achievements
    
    private val _currentStreak = MutableLiveData<Int>()
    val currentStreak: LiveData<Int> = _currentStreak
    
    /**
     * Load progress statistics for a user
     */
    fun loadProgressStats(userId: Long) {
        viewModelScope.launch {
            val recentWorkouts = workoutDao.getRecentWorkoutSessionsWithEntries(userId)
            recentWorkouts.collect { workoutList ->
                val stats = calculateProgressStats(workoutList)
                _workoutStats.value = stats
                
                // Calculate achievements
                val userAchievements = calculateAchievements(stats)
                _achievements.value = userAchievements
                
                // Calculate current streak
                val streak = calculateCurrentStreak(workoutList)
                _currentStreak.value = streak
            }
        }
    }
    
    /**
     * Calculate progress statistics from workout data
     */
    private fun calculateProgressStats(workouts: List<WorkoutSessionWithEntries>): ProgressStats {
        if (workouts.isEmpty()) {
            return ProgressStats(
                totalWorkouts = 0,
                totalDuration = 0,
                averageDuration = 0,
                weeklyWorkouts = 0,
                monthlyWorkouts = 0,
                lastWorkoutDate = null
            )
        }
        
        val totalWorkouts = workouts.size
        val totalDuration = workouts.sumOf { it.session.duration }
        val averageDuration = totalDuration / totalWorkouts
        
        // Calculate weekly and monthly workouts
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        
        val weeklyWorkouts = workouts.count { workout ->
            val workoutTime = workout.session.date.time
            val weekAgo = currentTime - (7 * 24 * 60 * 60 * 1000)
            workoutTime >= weekAgo
        }
        
        val monthlyWorkouts = workouts.count { workout ->
            val workoutTime = workout.session.date.time
            val monthAgo = currentTime - (30L * 24 * 60 * 60 * 1000)
            workoutTime >= monthAgo
        }
        
        return ProgressStats(
            totalWorkouts = totalWorkouts,
            totalDuration = totalDuration,
            averageDuration = averageDuration,
            weeklyWorkouts = weeklyWorkouts,
            monthlyWorkouts = monthlyWorkouts,
            lastWorkoutDate = workouts.first().session.date
        )
    }
    
    /**
     * Calculate achievements based on progress
     */
    private fun calculateAchievements(stats: ProgressStats): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        
        // First workout achievement
        if (stats.totalWorkouts >= 1) {
            achievements.add(Achievement("First Steps", "Completed your first workout!", "first_workout"))
        }
        
        // 5 workouts achievement
        if (stats.totalWorkouts >= 5) {
            achievements.add(Achievement("Getting Started", "Completed 5 workouts!", "five_workouts"))
        }
        
        // 10 workouts achievement
        if (stats.totalWorkouts >= 10) {
            achievements.add(Achievement("Consistency", "Completed 10 workouts!", "ten_workouts"))
        }
        
        // Weekly consistency achievement
        if (stats.weeklyWorkouts >= 3) {
            achievements.add(Achievement("Weekly Warrior", "Worked out 3+ times this week!", "weekly_warrior"))
        }
        
        // Monthly consistency achievement
        if (stats.monthlyWorkouts >= 12) {
            achievements.add(Achievement("Monthly Master", "Worked out 12+ times this month!", "monthly_master"))
        }
        
        // Duration achievements
        if (stats.totalDuration >= 300) { // 5 hours total
            achievements.add(Achievement("Time Warrior", "Spent 5+ hours working out!", "time_warrior"))
        }
        
        return achievements
    }
    
    /**
     * Calculate current workout streak
     */
    private fun calculateCurrentStreak(workouts: List<WorkoutSessionWithEntries>): Int {
        if (workouts.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val workoutDates = workouts.map { workout ->
            val workoutCalendar = Calendar.getInstance()
            workoutCalendar.time = workout.session.date
            workoutCalendar.set(Calendar.HOUR_OF_DAY, 0)
            workoutCalendar.set(Calendar.MINUTE, 0)
            workoutCalendar.set(Calendar.SECOND, 0)
            workoutCalendar.set(Calendar.MILLISECOND, 0)
            workoutCalendar.time
        }.distinct().sortedDescending()
        
        var streak = 0
        var currentDate = calendar.time
        
        for (workoutDate in workoutDates) {
            val daysDiff = (currentDate.time - workoutDate.time) / (24 * 60 * 60 * 1000)
            
            if (daysDiff <= 1) {
                streak++
                currentDate = workoutDate
            } else {
                break
            }
        }
        
        return streak
    }
}

/**
 * Data class for progress statistics
 */
data class ProgressStats(
    val totalWorkouts: Int,
    val totalDuration: Int,
    val averageDuration: Int,
    val weeklyWorkouts: Int,
    val monthlyWorkouts: Int,
    val lastWorkoutDate: Date?
)

/**
 * Data class for achievements
 */
data class Achievement(
    val title: String,
    val description: String,
    val iconName: String
) 