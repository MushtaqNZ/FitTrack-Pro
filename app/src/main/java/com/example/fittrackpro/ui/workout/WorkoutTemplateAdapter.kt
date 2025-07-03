package com.example.fittrackpro.ui.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fittrackpro.data.entity.WorkoutTemplate
import com.example.fittrackpro.databinding.ItemWorkoutTemplateBinding

/**
 * Adapter for displaying workout templates in a RecyclerView
 */
class WorkoutTemplateAdapter(
    private val onTemplateClick: (WorkoutTemplate) -> Unit
) : ListAdapter<WorkoutTemplate, WorkoutTemplateAdapter.TemplateViewHolder>(TemplateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding = ItemWorkoutTemplateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TemplateViewHolder(binding, onTemplateClick)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TemplateViewHolder(
        private val binding: ItemWorkoutTemplateBinding,
        private val onTemplateClick: (WorkoutTemplate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(template: WorkoutTemplate) {
            binding.tvTemplateName.text = template.name
            binding.tvTemplateDescription.text = template.description
            binding.tvTemplateCategory.text = template.category.capitalize()
            binding.tvTemplateDifficulty.text = template.difficultyLevel.capitalize()

            // Set category color
            val categoryColor = when (template.category) {
                "strength" -> android.graphics.Color.parseColor("#FF5722")
                "cardio" -> android.graphics.Color.parseColor("#4CAF50")
                "mixed" -> android.graphics.Color.parseColor("#2196F3")
                else -> android.graphics.Color.parseColor("#757575")
            }
            binding.tvTemplateCategory.setTextColor(categoryColor)

            // Set difficulty color
            val difficultyColor = when (template.difficultyLevel) {
                "beginner" -> android.graphics.Color.parseColor("#4CAF50")
                "intermediate" -> android.graphics.Color.parseColor("#FF9800")
                "advanced" -> android.graphics.Color.parseColor("#F44336")
                else -> android.graphics.Color.parseColor("#757575")
            }
            binding.tvTemplateDifficulty.setTextColor(difficultyColor)

            // Set click listener
            binding.root.setOnClickListener {
                onTemplateClick(template)
            }
        }
    }

    private class TemplateDiffCallback : DiffUtil.ItemCallback<WorkoutTemplate>() {
        override fun areItemsTheSame(oldItem: WorkoutTemplate, newItem: WorkoutTemplate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WorkoutTemplate, newItem: WorkoutTemplate): Boolean {
            return oldItem == newItem
        }
    }
} 