package com.stop.ui.route

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.RouteItemBinding
import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.TransportRoute

class RouteViewHolder(
    private val binding: RouteItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = TimeLineAdapter()

    init {
        binding.recyclerviewTimeLine.adapter = adapter
        binding.recyclerviewTimeLine.setHasFixedSize(true)
        binding.recyclerviewTimeLine.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (parent.getChildAdapterPosition(view) != RecyclerView.NO_POSITION) {
                    outRect.set(0, 0, -40, 0)
                }
            }
        })
    }

    fun bind(itinerary: Itinerary) {
        binding.textViewExpectedRequiredTime.text = secondToHourAndMinute(itinerary.totalTime)

        adapter.submitList(itinerary.routes)
    }

    private fun calculateExpectedRoute(itinerary: Itinerary): String {
        return itinerary.routes.joinToString(" -> ") { route ->
            when (route.mode) {
                MoveType.SUBWAY -> "${(route as TransportRoute).routeInfo} ${route.start.name}역"
                MoveType.BUS -> {
                    val (busType, busNum) = (route as TransportRoute).routeInfo.split(":")
                    "${route.start.name} 정거장 $busType $busNum 버스"
                }
                else -> route.start.name
            }
        } + " -> 도착지"
    }

    private fun secondToHourAndMinute(second: Int): String {
        return "${second / 60 / 60}시간 ${second / 60 % 60}분"
    }
}