package com.stop.ui.routedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.RoutePathItemBinding
import com.stop.domain.model.route.tmap.custom.Route

class RouteDetailAdapter(
    // private val onRouteDetailClickListener: OnRouteDetailClickListener
): ListAdapter<Route, RecyclerView.ViewHolder>(diffUtil) {
    class PathViewHolder(
        private val binding: RoutePathItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route) {
            binding.route = route
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
        private val diffUtil = object : DiffUtil.ItemCallback<Route>() {
            override fun areItemsTheSame(oldRoute: Route, newRoute: Route): Boolean {
                return oldRoute.start.name == newRoute.start.name
            }

            override fun areContentsTheSame(oldRoute: Route, newRoute: Route): Boolean {
                return oldRoute::class == newRoute::class
            }
        }
    }
}