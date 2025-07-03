package com.example.fittrackpro.data.dao

import androidx.room.*
import com.example.fittrackpro.data.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User entity
 * Provides database operations for user management
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
} 