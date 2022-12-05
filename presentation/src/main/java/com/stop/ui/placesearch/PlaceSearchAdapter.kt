package com.stop.ui.placesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.ItemNearPlaceBinding
import com.stop.domain.model.nearplace.PlaceUseCaseItem

class PlaceSearchAdapter(
    private val onItemClick: (PlaceUseCaseItem) -> Unit
) : ListAdapter<PlaceUseCaseItem, PlaceSearchAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(private val binding: ItemNearPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(placeUseCaseItem: PlaceUseCaseItem) {
            binding.place = placeUseCaseItem
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNearPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentList[holder.adapterPosition])
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PlaceUseCaseItem>() {
            override fun areItemsTheSame(oldItem: PlaceUseCaseItem, newItem: PlaceUseCaseItem): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: PlaceUseCaseItem, newItem: PlaceUseCaseItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}