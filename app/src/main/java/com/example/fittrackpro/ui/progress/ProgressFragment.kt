package com.example.fittrackpro.ui.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittrackpro.databinding.FragmentProgressBinding
import com.example.fittrackpro.viewmodel.ProgressViewModel
import com.example.fittrackpro.viewmodel.UserViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.text.SimpleDateFormat
import java.util.*

/**
 * Progress fragment for displaying workout statistics and achievements
 * Shows progress charts, streaks, and achievement badges
 */
class ProgressFragment : Fragment() {
    
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userViewModel: UserViewModel
    private lateinit var progressViewModel: ProgressViewModel
    private lateinit var achievementAdapter: AchievementAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModels
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        progressViewModel = ViewModelProvider(requireActivity())[ProgressViewModel::class.java]
        
        setupUI()
        setupObservers()
        loadData()
    }
    
    private fun setupUI() {
        // Setup achievements recycler view
        achievementAdapter = AchievementAdapter()
        binding.recyclerAchievements.layoutManager = LinearLayoutManager(context)
        binding.recyclerAchievements.adapter = achievementAdapter
        
        // Setup progress chart
        setupProgressChart()
    }
    
    private fun setupProgressChart() {
        // Configure chart appearance
        binding.chartProgress.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            
            xAxis.apply {
                setDrawGridLines(false)
                setDrawAxisLine(true)
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(true)
            }
            
            axisRight.isEnabled = false
        }
    }
    
    private fun setupObservers() {
        // Observe current user
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                progressViewModel.loadProgressStats(it.id)
            }
        }
        
        // Observe progress stats
        progressViewModel.workoutStats.observe(viewLifecycleOwner) { stats ->
            updateProgressStats(stats)
            updateProgressChart(stats)
        }
        
        // Observe current streak
        progressViewModel.currentStreak.observe(viewLifecycleOwner) { streak ->
            binding.tvCurrentStreak.text = streak.toString()
        }
        
        // Observe achievements
        progressViewModel.achievements.observe(viewLifecycleOwner) { achievements ->
            achievementAdapter.submitList(achievements)
            binding.tvAchievementCount.text = "${achievements.size} Achievements"
        }
    }
    
    private fun loadData() {
        // Data will be loaded when user is available
    }
    
    private fun updateProgressStats(stats: com.example.fittrackpro.viewmodel.ProgressStats) {
        binding.tvTotalWorkouts.text = stats.totalWorkouts.toString()
        binding.tvTotalDuration.text = "${stats.totalDuration} min"
        binding.tvAverageDuration.text = "${stats.averageDuration} min"
        binding.tvWeeklyWorkouts.text = stats.weeklyWorkouts.toString()
        binding.tvMonthlyWorkouts.text = stats.monthlyWorkouts.toString()
        
        // Format last workout date
        stats.lastWorkoutDate?.let { date ->
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvLastWorkout.text = dateFormat.format(date)
        } ?: run {
            binding.tvLastWorkout.text = "No workouts yet"
        }
    }
    
    private fun updateProgressChart(stats: com.example.fittrackpro.viewmodel.ProgressStats) {
        // Create sample data for the chart (in a real app, this would be actual workout data)
        val entries = mutableListOf<BarEntry>()
        
        // Add sample weekly data
        entries.add(BarEntry(0f, stats.weeklyWorkouts.toFloat()))
        entries.add(BarEntry(1f, stats.monthlyWorkouts.toFloat()))
        entries.add(BarEntry(2f, stats.totalWorkouts.toFloat()))
        
        val dataSet = BarDataSet(entries, "Workouts")
        dataSet.color = requireContext().getColor(com.example.fittrackpro.R.color.primary)
        
        val barData = BarData(dataSet)
        binding.chartProgress.data = barData
        binding.chartProgress.invalidate()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 