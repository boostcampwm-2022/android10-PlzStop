package com.stop.ui.routedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.RouteFirstItemBinding
import com.stop.databinding.RouteLastItemBinding
import com.stop.databinding.RoutePathItemBinding
import com.stop.model.route.RouteItem
import com.stop.model.route.RouteItemType

class RouteDetailAdapter(
    private val onRouteItemClickListener: OnRouteItemClickListener
): ListAdapter<RouteItem, RecyclerView.ViewHolder>(diffUtil) {
    class FirstViewHolder(
        private val binding: RouteFirstItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routeItem: RouteItem) {
            binding.routeItem = routeItem
        }
    }

    class PathViewHolder(
        private val binding: RoutePathItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routeItem: RouteItem) {
            binding.routeItem = routeItem
        }
    }

    class LastViewHolder(
        private val binding: RouteLastItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routeItem: RouteItem) {
            binding.routeItem = routeItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ViewDataBinding
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            TYPE_FIRST -> {
                binding = RouteFirstItemBinding.inflate(inflater, parent, false)
                FirstViewHolder(binding)
            }
            TYPE_PATH -> {
                binding = RoutePathItemBinding.inflate(inflater, parent, false)
                PathViewHolder(binding)
            }
            TYPE_LAST -> {
                binding = RouteLastItemBinding.inflate(inflater, parent, false)
                LastViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid ViewType")
        }

        binding.root.setOnClickListener {
            onRouteItemClickListener.clickRouteItem(getItem(viewHolder.adapterPosition).coordinate)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FirstViewHolder -> holder.bind(getItem(position))
            is PathViewHolder -> holder.bind(getItem(position))
            is LastViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when ((getItem(position) as RouteItem).type) {
            RouteItemType.FIRST -> TYPE_FIRST
            RouteItemType.PATH -> TYPE_PATH
            RouteItemType.LAST -> TYPE_LAST
        }
    }

    companion object {
        private const val TYPE_FIRST = 0
        private const val TYPE_PATH = 1
        private const val TYPE_LAST = 2

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