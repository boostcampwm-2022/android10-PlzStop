package com.stop.ui.routedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.RoutePathItemBinding
import com.stop.model.route.RouteItem

class RouteDetailAdapter(
    // private val onRouteDetailClickListener: OnRouteDetailClickListener
): ListAdapter<RouteItem, RecyclerView.ViewHolder>(diffUtil) {
    class PathViewHolder(
        private val binding: RoutePathItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routeItem: RouteItem) {
            binding.routeItem = routeItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PathViewHolder(RoutePathItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PathViewHolder).bind(getItem(position))
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