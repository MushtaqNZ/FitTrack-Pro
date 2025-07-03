package com.example.fittrackpro.data

import android.content.Context
import androidx.room.*
import com.example.fittrackpro.data.dao.ExerciseDao
import com.example.fittrackpro.data.dao.UserDao
import com.example.fittrackpro.data.dao.WorkoutDao
import com.example.fittrackpro.data.entity.*
import java.util.concurrent.Executors

/**
 * Main Room database for FitTrack Pro
 * Contains all entities and provides access to DAOs
 */
@Database(
    entities = [
        User::class,
        WorkoutTemplate::class,
        Exercise::class,
        WorkoutSession::class,
        WorkoutEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitTrackDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    
    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null
        
        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Populate database with initial data
                        Executors.newSingleThreadExecutor().execute {
                            getDatabase(context).let { database ->
                                DatabaseInitializer.populateDatabase(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Type converters for Room database
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): java.util.Date? {
        return value?.let { java.util.Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: java.util.Date?): Long? {
        return date?.time
    }
} 