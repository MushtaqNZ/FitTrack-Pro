package com.example.fittrackpro.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fittrackpro.R
import com.example.fittrackpro.databinding.FragmentProfileBinding
import com.example.fittrackpro.viewmodel.UserViewModel

/**
 * Profile fragment for user profile management
 * Allows users to view and edit their profile information
 */
class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        
        setupUI()
        setupObservers()
        loadData()
    }
    
    private fun setupUI() {
        // Setup fitness level spinner
        val fitnessLevels = arrayOf("Beginner", "Intermediate", "Advanced")
        val fitnessLevelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fitnessLevels)
        fitnessLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFitnessLevel.adapter = fitnessLevelAdapter
        
        // Setup workout type spinner
        val workoutTypes = arrayOf("Strength", "Cardio", "Mixed")
        val workoutTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutTypes)
        workoutTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkoutType.adapter = workoutTypeAdapter
        
        // Setup save button
        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
        
        // Setup edit mode toggle
        binding.btnEditProfile.setOnClickListener {
            toggleEditMode(true)
        }
    }
    
    private fun setupObservers() {
        // Observe current user
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                displayUserProfile(it)
            }
        }
    }
    
    private fun loadData() {
        // User data will be loaded when available
    }
    
    private fun displayUserProfile(user: com.example.fittrackpro.data.entity.User) {
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        
        // Set fitness level spinner
        val fitnessLevelIndex = when (user.fitnessLevel) {
            "beginner" -> 0
            "intermediate" -> 1
            "advanced" -> 2
            else -> 0
        }
        binding.spinnerFitnessLevel.setSelection(fitnessLevelIndex)
        
        // Set workout type spinner
        val workoutTypeIndex = when (user.preferredWorkoutType) {
            "strength" -> 0
            "cardio" -> 1
            "mixed" -> 2
            else -> 0
        }
        binding.spinnerWorkoutType.setSelection(workoutTypeIndex)
        
        // Initially disable editing
        toggleEditMode(false)
    }
    
    private fun toggleEditMode(enabled: Boolean) {
        binding.etName.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
        binding.spinnerFitnessLevel.isEnabled = enabled
        binding.spinnerWorkoutType.isEnabled = enabled
        binding.btnSaveProfile.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.btnEditProfile.visibility = if (enabled) View.GONE else View.VISIBLE
    }
    
    private fun saveProfile() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        
        if (name.isBlank() || email.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val fitnessLevel = when (binding.spinnerFitnessLevel.selectedItemPosition) {
            0 -> "beginner"
            1 -> "intermediate"
            2 -> "advanced"
            else -> "beginner"
        }
        
        val workoutType = when (binding.spinnerWorkoutType.selectedItemPosition) {
            0 -> "strength"
            1 -> "cardio"
            2 -> "mixed"
            else -> "strength"
        }
        
        userViewModel.currentUser.value?.let { currentUser ->
            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                fitnessLevel = fitnessLevel,
                preferredWorkoutType = workoutType
            )
            
            userViewModel.updateUser(updatedUser)
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            toggleEditMode(false)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 