package com.stop.ui.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.TimeLineItemBinding
import com.stop.domain.model.route.tmap.custom.Route
import com.stop.domain.model.route.tmap.custom.TransportRoute
import com.stop.domain.model.route.tmap.custom.WalkRoute

class TimeLineAdapter : ListAdapter<Route, TimeLineViewHolder>(diffUtil) {

    lateinit var recyclerView: RecyclerView
    var width: Int? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun onViewAttachedToWindow(holder: TimeLineViewHolder) {
        super.onViewAttachedToWindow(holder)

        val currentWidth = width ?: recyclerView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        ).let {
            width = recyclerView.measuredWidth
            recyclerView.measuredWidth
        }
        holder.adjustViewHolderSize(currentWidth)
    }

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