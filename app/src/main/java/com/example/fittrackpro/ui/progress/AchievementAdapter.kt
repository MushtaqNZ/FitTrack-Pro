package com.example.fittrackpro.ui.progress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fittrackpro.databinding.ItemAchievementBinding
import com.example.fittrackpro.viewmodel.Achievement
import com.example.fittrackpro.R

/**
 * Adapter for displaying achievements in a RecyclerView
 */
class AchievementAdapter : ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.tvAchievementTitle.text = achievement.title
            binding.tvAchievementDescription.text = achievement.description
            
            // Set achievement icon based on type
            val iconRes = when (achievement.iconName) {
                "first_workout" -> android.R.drawable.ic_menu_send
                "five_workouts" -> android.R.drawable.ic_menu_share
                "ten_workouts" -> android.R.drawable.ic_menu_agenda
                "weekly_warrior" -> android.R.drawable.ic_menu_today
                "monthly_master" -> android.R.drawable.ic_menu_month
                "time_warrior" -> android.R.drawable.ic_menu_recent_history
                else -> R.drawable.ic_star
            }
            
            binding.ivAchievementIcon.setImageResource(iconRes)
        }
    }

    private class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
} 