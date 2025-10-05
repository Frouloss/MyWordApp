package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class LevelsAdapter(
    private val levels: List<Level>,
    private val onClick: (Level) -> Unit
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLevelNumber: TextView = itemView.findViewById(R.id.tvLevelNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_level, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levels[position]
        holder.tvLevelNumber.text = level.number.toString()

        if (!level.isUnlocked) {
            holder.tvLevelNumber.setTextColor(Color.GRAY)
        }

        holder.itemView.setOnClickListener {
            if (level.isUnlocked) {
                onClick(level)
            } else {
                Toast.makeText(holder.itemView.context, "Уровень закрыт!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = levels.size
}
