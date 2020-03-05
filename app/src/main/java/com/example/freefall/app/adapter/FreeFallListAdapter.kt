package com.example.freefall.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.freefall.R
import com.example.frefalllib.db.FallObject
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_fall_list.view.*

/**
 * @author Tomislav Curis
 */
class FreeFallListAdapter() : ListAdapter<FallObject, FreeFallListViewHolder>(FreeFallDiffUtil()) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeFallListViewHolder
            = FreeFallListViewHolder(LayoutInflater.from(parent.context).inflate
        (R.layout.item_fall_list, parent, false))

    override fun getItemId(position: Int): Long = getItem(position).timestamp.hashCode().toLong()

    override fun onBindViewHolder(holder: FreeFallListViewHolder, position: Int) {

        holder.itemView.iitemFallTvTimestamp.text = getItem(position).timestamp
        holder.itemView.itemFallTvDuration.text = getItem(position).fallDuration + " ms"

    }

    override fun onViewRecycled(holder: FreeFallListViewHolder) {
        holder.unbindView()
        super.onViewRecycled(holder)
    }

}

class FreeFallListViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
    override val containerView: View? = view

    fun bindView() {}

    fun unbindView() {}
}
