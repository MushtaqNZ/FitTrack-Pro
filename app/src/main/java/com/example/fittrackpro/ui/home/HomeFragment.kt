package com.example.fittrackpro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fittrackpro.R
import com.example.fittrackpro.databinding.FragmentHomeBinding
import com.example.fittrackpro.ui.workout.WorkoutFragment
import com.example.fittrackpro.viewmodel.ProgressViewModel
import com.example.fittrackpro.viewmodel.UserViewModel
import com.example.fittrackpro.viewmodel.WorkoutViewModel
import com.google.android.material.button.MaterialButton

/**
 * Home fragment serving as the main dashboard
 * Shows welcome message, quick start options, and recent workouts
 */
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var progressViewModel: ProgressViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModels
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class.java]
        progressViewModel = ViewModelProvider(requireActivity())[ProgressViewModel::class.java]
        
        setupUI()
        setupObservers()
        loadData()
    }
    
    private fun setupUI() {
        // Quick start button
        binding.btnQuickStart.setOnClickListener {
            // Navigate to workout fragment
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as androidx.navigation.fragment.NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.navigation_workout)
        }
        
        // Recent workouts recycler view
        binding.recyclerRecentWorkouts.layoutManager = LinearLayoutManager(context)
        // TODO: Add adapter for recent workouts
    }
    
    private fun setupObservers() {
        // Observe current user
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvWelcomeMessage.text = getString(R.string.welcome_message) + " ${it.name}!"
                // Load user's recent workouts
                workoutViewModel.loadRecentWorkouts(it.id)
                // Load user's progress stats
                progressViewModel.loadProgressStats(it.id)
            }
        }
        
        // Observe recent workouts
        workoutViewModel.recentWorkouts.observe(viewLifecycleOwner) { workouts ->
            if (workouts.isEmpty()) {
                binding.tvNoWorkouts.visibility = View.VISIBLE
                binding.recyclerRecentWorkouts.visibility = View.GONE
            } else {
                binding.tvNoWorkouts.visibility = View.GONE
                binding.recyclerRecentWorkouts.visibility = View.VISIBLE
                // TODO: Update recycler view with workouts
            }
        }
        
        // Observe progress stats
        progressViewModel.workoutStats.observe(viewLifecycleOwner) { stats ->
            binding.tvTotalWorkouts.text = stats.totalWorkouts.toString()
            binding.tvCurrentStreak.text = stats.totalWorkouts.toString() // Placeholder
        }
        
        progressViewModel.currentStreak.observe(viewLifecycleOwner) { streak ->
            binding.tvCurrentStreak.text = streak.toString()
        }
    }
    
    private fun loadData() {
        // Check if user exists, if not create default user
        userViewModel.checkUserExists()
        userViewModel.isUserCreated.observe(viewLifecycleOwner) { exists ->
            if (!exists) {
                // Create default user for MVP
                userViewModel.createUser(
                    name = "Fitness User",
                    email = "user@fittrackpro.com",
                    fitnessLevel = "beginner",
                    preferredWorkoutType = "strength"
                )
            } else {
                userViewModel.getFirstUser()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 