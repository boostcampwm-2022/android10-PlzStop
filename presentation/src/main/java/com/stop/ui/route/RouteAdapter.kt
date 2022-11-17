package com.stop.ui.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.stop.databinding.RouteItemBinding
import com.stop.domain.model.Itinerary

class RouteAdapter : ListAdapter<Itinerary, RouteViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder(
            RouteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Itinerary>() {
            override fun areItemsTheSame(
                oldItinerary: Itinerary,
                newItinerary: Itinerary
            ): Boolean {
                return oldItinerary.routes == newItinerary.routes
            }

            override fun areContentsTheSame(
                oldItinerary: Itinerary,
                newItinerary: Itinerary
            ): Boolean {
                return oldItinerary == newItinerary
            }
        }
    }
}