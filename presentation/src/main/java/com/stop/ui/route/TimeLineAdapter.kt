package com.stop.ui.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.stop.databinding.TimeLineItemBinding
import com.stop.domain.model.route.tmap.custom.Route
import com.stop.domain.model.route.tmap.custom.TransportRoute
import com.stop.domain.model.route.tmap.custom.WalkRoute

class TimeLineAdapter : ListAdapter<Route, TimeLineViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val binding = TimeLineItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TimeLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.bind(getItem(position), holder.adapterPosition)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Route>() {
            override fun areItemsTheSame(oldRoute: Route, newRoute: Route): Boolean {
                return oldRoute.sectionTime == newRoute.sectionTime &&
                        oldRoute.mode == newRoute.mode
            }

            override fun areContentsTheSame(oldRoute: Route, newRoute: Route): Boolean {
                if (oldRoute is TransportRoute && newRoute is TransportRoute) {
                    return oldRoute == newRoute
                } else if (oldRoute is WalkRoute && newRoute is WalkRoute) {
                    return oldRoute == newRoute
                }
                return false
            }
        }
    }
}