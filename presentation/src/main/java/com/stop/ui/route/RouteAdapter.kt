package com.stop.ui.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.RouteItemBinding
import com.stop.domain.model.route.tmap.custom.Itinerary

class RouteAdapter(
    private val onItineraryClickListener: OnItineraryClickListener
) : ListAdapter<Itinerary, RouteViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = RouteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = RouteViewHolder(binding)
        binding.root.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            onItineraryClickListener.onItineraryClick(getItem(position))
        }

        return viewHolder
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
                return oldItinerary.totalTime == newItinerary.totalTime
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