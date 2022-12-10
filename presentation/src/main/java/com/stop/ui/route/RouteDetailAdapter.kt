package com.stop.ui.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.RoutePathAtTimeLineItemBinding
import com.stop.model.route.RouteItem

class RouteDetailAdapter : ListAdapter<RouteItem, RouteDetailAdapter.PathViewHolder>(diffUtil) {

    class PathViewHolder(
        private val binding: RoutePathAtTimeLineItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(routeItem: RouteItem, position: Int) {
            binding.routeItem = routeItem
            binding.executePendingBindings()

            if (position == 0) {
                binding.viewBeforeLine.visibility = View.INVISIBLE
                return
            }
            binding.viewBeforeLine.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RoutePathAtTimeLineItemBinding.inflate(inflater, parent, false)

        return PathViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<RouteItem>() {
            override fun areItemsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}