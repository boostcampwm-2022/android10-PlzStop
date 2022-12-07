package com.stop.ui.placesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.ItemRecentSearchBinding
import com.stop.domain.model.nearplace.PlaceUseCaseItem

class RecentPlaceSearchAdapter(
    private var onItemClick: (PlaceUseCaseItem) -> Unit
) : ListAdapter<PlaceUseCaseItem, RecentPlaceSearchAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(private val binding: ItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(placeUseCaseItem: PlaceUseCaseItem) {
            binding.placeItem = placeUseCaseItem
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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