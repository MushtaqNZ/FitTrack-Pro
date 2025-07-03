package com.example.fittrackpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittrackpro.data.FitTrackDatabase
import com.example.fittrackpro.data.entity.User
import kotlinx.coroutines.launch

/**
 * ViewModel for user management operations
 * Handles user profile creation, updates, and retrieval
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = FitTrackDatabase.getDatabase(application)
    private val userDao = database.userDao()
    
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    private val _isUserCreated = MutableLiveData<Boolean>()
    val isUserCreated: LiveData<Boolean> = _isUserCreated
    
    /**
     * Create a new user profile
     */
    fun createUser(name: String, email: String, fitnessLevel: String, preferredWorkoutType: String) {
        viewModelScope.launch {
            val user = User(
                name = name,
                email = email,
                fitnessLevel = fitnessLevel,
                preferredWorkoutType = preferredWorkoutType
            )
            
            val userId = userDao.insertUser(user)
            val createdUser = user.copy(id = userId)
            _currentUser.value = createdUser
            _isUserCreated.value = true
        }
    }
    
    /**
     * Load user by ID
     */
    fun loadUser(userId: Long) {
        viewModelScope.launch {
            val user = userDao.getUserById(userId)
            _currentUser.value = user
        }
    }
    
    /**
     * Update user profile
     */
    fun updateUser(user: User) {
        viewModelScope.launch {
            userDao.updateUser(user)
            _currentUser.value = user
        }
    }
    
    /**
     * Check if any user exists in the database
     */
    fun checkUserExists() {
        viewModelScope.launch {
            val userCount = userDao.getUserCount()
            _isUserCreated.value = userCount > 0
        }
    }
    
    /**
     * Get the first user (for MVP, we'll assume single user)
     */
    fun getFirstUser() {
        viewModelScope.launch {
            // For MVP, we'll get the first user from the database
            // In a real app, you'd have proper user authentication
            val users = userDao.getAllUsers()
            users.collect { userList ->
                if (userList.isNotEmpty()) {
                    _currentUser.value = userList.first()
                    _isUserCreated.value = true
                } else {
                    _isUserCreated.value = false
                }
            }
        }
    }
} 