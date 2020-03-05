package com.example.freefall.app.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.frefalllib.db.FallObject

/**
 * @author Tomislav Curis
 */
class FreeFallDiffUtil : DiffUtil.ItemCallback<FallObject>() {

    override fun areItemsTheSame(oldItem: FallObject, newItem: FallObject): Boolean {
        return oldItem.fallDuration == newItem.fallDuration && oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: FallObject, newItem: FallObject): Boolean {
        return oldItem.fallDuration == newItem.fallDuration && oldItem.timestamp == newItem.timestamp
    }
}