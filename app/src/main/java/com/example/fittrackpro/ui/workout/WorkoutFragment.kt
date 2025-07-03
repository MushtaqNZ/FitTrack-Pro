package com.example.fittrackpro.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittrackpro.R
import com.example.fittrackpro.databinding.FragmentWorkoutBinding
import com.example.fittrackpro.viewmodel.UserViewModel
import com.example.fittrackpro.viewmodel.WorkoutViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Workout fragment for logging workouts and selecting templates
 * Handles workout session creation and exercise logging
 */
class WorkoutFragment : Fragment() {
    
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var workoutTemplateAdapter: WorkoutTemplateAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModels
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class.java]
        
        setupUI()
        setupObservers()
        loadData()
    }
    
    private fun setupUI() {
        // Setup workout templates recycler view
        workoutTemplateAdapter = WorkoutTemplateAdapter { template ->
            showWorkoutLoggingDialog(template)
        }
        binding.recyclerWorkoutTemplates.layoutManager = LinearLayoutManager(context)
        binding.recyclerWorkoutTemplates.adapter = workoutTemplateAdapter
        
        // Setup category filter spinner
        val categories = arrayOf("All", "Strength", "Cardio", "Mixed")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter
        
        binding.spinnerCategory.setOnItemSelectedListener { _, _, position, _ ->
            when (position) {
                0 -> workoutViewModel.loadWorkoutTemplates()
                1 -> workoutViewModel.loadWorkoutTemplatesByCategory("strength")
                2 -> workoutViewModel.loadWorkoutTemplatesByCategory("cardio")
                3 -> workoutViewModel.loadWorkoutTemplatesByCategory("mixed")
            }
        }
        
        // Setup exercise library button
        binding.btnExerciseLibrary.setOnClickListener {
            showExerciseLibrary()
        }
    }
    
    private fun setupObservers() {
        // Observe workout templates
        workoutViewModel.workoutTemplates.observe(viewLifecycleOwner) { templates ->
            workoutTemplateAdapter.submitList(templates)
        }
        
        // Observe workout save status
        workoutViewModel.isWorkoutSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(context, "Workout saved successfully!", Toast.LENGTH_SHORT).show()
                clearWorkoutForm()
            } else {
                Toast.makeText(context, "Failed to save workout", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadData() {
        // Load workout templates
        workoutViewModel.loadWorkoutTemplates()
        
        // Load exercises for library
        workoutViewModel.loadExercises()
    }
    
    private fun showWorkoutLoggingDialog(template: com.example.fittrackpro.data.entity.WorkoutTemplate) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_workout_logging, null)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Log Workout: ${template.name}")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Get form data and save workout
                val durationText = dialogView.findViewById<android.widget.EditText>(R.id.et_duration).text.toString()
                val notes = dialogView.findViewById<android.widget.EditText>(R.id.et_notes).text.toString()
                
                val duration = durationText.toIntOrNull() ?: 30
                
                userViewModel.currentUser.value?.let { user ->
                    // For MVP, we'll create a simple workout with default exercises
                    val defaultExercises = listOf(
                        Triple(1L, 3, 0f), // Push-ups, 3 sets, bodyweight
                        Triple(2L, 3, 0f), // Squats, 3 sets, bodyweight
                        Triple(3L, 3, 0f)  // Plank, 3 sets, bodyweight
                    )
                    
                    workoutViewModel.saveWorkoutSession(
                        userId = user.id,
                        templateId = template.id,
                        duration = duration,
                        notes = notes,
                        entries = defaultExercises
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showExerciseLibrary() {
        val exercises = workoutViewModel.exercises.value ?: emptyList()
        
        val exerciseNames = exercises.map { it.name }.toTypedArray()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Exercise Library")
            .setItems(exerciseNames) { _, which ->
                val selectedExercise = exercises[which]
                showExerciseDetails(selectedExercise)
            }
            .setPositiveButton("Close", null)
            .show()
    }
    
    private fun showExerciseDetails(exercise: com.example.fittrackpro.data.entity.Exercise) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(exercise.name)
            .setMessage("""
                Description: ${exercise.description}
                Muscle Group: ${exercise.muscleGroup}
                Equipment: ${exercise.equipmentNeeded}
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun clearWorkoutForm() {
        // Clear any form data if needed
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 