package com.example.fittrackpro.data

import com.example.fittrackpro.data.entity.Exercise
import com.example.fittrackpro.data.entity.WorkoutTemplate

/**
 * Database initializer to populate the database with initial data
 * Provides workout templates and exercise library
 */
object DatabaseInitializer {
    
    fun populateDatabase(database: FitTrackDatabase) {
        // Insert workout templates
        val workoutTemplates = listOf(
            WorkoutTemplate(
                name = "Full Body Strength",
                description = "Complete full body workout targeting all major muscle groups",
                category = "strength",
                difficultyLevel = "intermediate"
            ),
            WorkoutTemplate(
                name = "Upper Body Focus",
                description = "Concentrated upper body workout for chest, back, and arms",
                category = "strength",
                difficultyLevel = "beginner"
            ),
            WorkoutTemplate(
                name = "Lower Body Power",
                description = "Intense lower body workout focusing on legs and glutes",
                category = "strength",
                difficultyLevel = "intermediate"
            ),
            WorkoutTemplate(
                name = "Cardio Blast",
                description = "High-intensity cardio workout for endurance and fat burning",
                category = "cardio",
                difficultyLevel = "beginner"
            ),
            WorkoutTemplate(
                name = "Mixed Circuit",
                description = "Combination of strength and cardio exercises in circuit format",
                category = "mixed",
                difficultyLevel = "advanced"
            )
        )
        
        // Insert exercises
        val exercises = listOf(
            // Chest exercises
            Exercise(
                name = "Push-ups",
                description = "Classic bodyweight exercise for chest, shoulders, and triceps",
                muscleGroup = "chest",
                equipmentNeeded = "bodyweight"
            ),
            Exercise(
                name = "Bench Press",
                description = "Compound exercise for chest development using barbell",
                muscleGroup = "chest",
                equipmentNeeded = "barbell"
            ),
            Exercise(
                name = "Dumbbell Flyes",
                description = "Isolation exercise for chest muscles using dumbbells",
                muscleGroup = "chest",
                equipmentNeeded = "dumbbells"
            ),
            
            // Back exercises
            Exercise(
                name = "Pull-ups",
                description = "Upper body pulling exercise for back and biceps",
                muscleGroup = "back",
                equipmentNeeded = "bodyweight"
            ),
            Exercise(
                name = "Deadlift",
                description = "Compound exercise for overall back and posterior chain",
                muscleGroup = "back",
                equipmentNeeded = "barbell"
            ),
            Exercise(
                name = "Bent-over Rows",
                description = "Back exercise using barbell or dumbbells",
                muscleGroup = "back",
                equipmentNeeded = "barbell"
            ),
            
            // Leg exercises
            Exercise(
                name = "Squats",
                description = "Fundamental leg exercise for quadriceps and glutes",
                muscleGroup = "legs",
                equipmentNeeded = "bodyweight"
            ),
            Exercise(
                name = "Lunges",
                description = "Unilateral leg exercise for balance and strength",
                muscleGroup = "legs",
                equipmentNeeded = "bodyweight"
            ),
            Exercise(
                name = "Leg Press",
                description = "Machine-based leg exercise for quadriceps",
                muscleGroup = "legs",
                equipmentNeeded = "machine"
            ),
            
            // Shoulder exercises
            Exercise(
                name = "Overhead Press",
                description = "Compound shoulder exercise using barbell or dumbbells",
                muscleGroup = "shoulders",
                equipmentNeeded = "barbell"
            ),
            Exercise(
                name = "Lateral Raises",
                description = "Isolation exercise for lateral deltoids",
                muscleGroup = "shoulders",
                equipmentNeeded = "dumbbells"
            ),
            
            // Arm exercises
            Exercise(
                name = "Bicep Curls",
                description = "Isolation exercise for biceps using dumbbells",
                muscleGroup = "arms",
                equipmentNeeded = "dumbbells"
            ),
            Exercise(
                name = "Tricep Dips",
                description = "Bodyweight exercise for triceps",
                muscleGroup = "arms",
                equipmentNeeded = "bodyweight"
            ),
            
            // Core exercises
            Exercise(
                name = "Plank",
                description = "Isometric core exercise for stability",
                muscleGroup = "core",
                equipmentNeeded = "bodyweight"
            ),
            Exercise(
                name = "Crunches",
                description = "Basic abdominal exercise",
                muscleGroup = "core",
                equipmentNeeded = "bodyweight"
            ),
            Exercise(
                name = "Russian Twists",
                description = "Rotational core exercise",
                muscleGroup = "core",
                equipmentNeeded = "bodyweight"
            )
        )
        
        // Insert data into database
        database.workoutDao().let { workoutDao ->
            workoutTemplates.forEach { template ->
                workoutDao.insertWorkoutTemplate(template)
            }
        }
        
        database.exerciseDao().let { exerciseDao ->
            exerciseDao.insertExercises(exercises)
        }
    }
} 