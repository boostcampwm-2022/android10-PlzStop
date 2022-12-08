package com.stop.ui.route

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stop.R
import com.stop.databinding.RouteItemBinding
import com.stop.domain.model.route.tmap.custom.*
import com.stop.model.route.RouteItem
import com.stop.model.route.RouteItemType

class RouteViewHolder(
    private val binding: RouteItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = RouteDetailAdapter()
    var routeItemColor = 0

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
                    outRect.set(0, 0, -10, 0)
                }
            }
        })
    }

    fun bind(itinerary: Itinerary) {
        binding.textViewExpectedRequiredTime.text = secondToHourAndMinute(itinerary.totalTime)
        val routeItems = mutableListOf<RouteItem>()

        itinerary.routes.forEach { route ->
            routeItems.add(
                RouteItem(
                    name = route.start.name,
                    coordinate = route.start.coordinate,
                    mode = getRouteItemMode(route),
                    distance = route.distance,
                    travelTime = route.sectionTime.toInt(),
                    lastTime = "",
                    beforeColor = getRouteItemColor(route, false),
                    currentColor = getRouteItemColor(route, true),
                    type = RouteItemType.PATH,
                    typeName = getTypeName(route),
                )
            )
        }
        adapter.submitList(routeItems)
        binding.timeLineContainer.submitList(itinerary.routes)
    }

    private fun getTypeName(route: Route): String {
        return when(route) {
            is WalkRoute -> "도보"
            is TransportRoute -> getSubwayTypeName(route)
            else -> ""
        }
    }

    private fun getSubwayTypeName(route: TransportRoute): String {
        return when(route.mode) {
            MoveType.SUBWAY -> route.routeInfo.replace("수도권", "")
            MoveType.BUS -> route.routeInfo.split(":")[1]
            else -> route.routeInfo
        }
    }

    private fun getRouteItemColor(route: Route, isCurrent: Boolean): Int {
        return if (isCurrent) {
            when (route) {
                is TransportRoute -> Color.parseColor("#${route.routeColor}")
                is WalkRoute -> ContextCompat.getColor(binding.root.context, R.color.main_yellow)
                else -> ContextCompat.getColor(binding.root.context, R.color.main_lighter_grey)
            }
        } else {
            if (routeItemColor != 0) {
                routeItemColor
            } else {
                getRouteItemColor(route, true)
            }
        }
    }

    private fun getRouteItemMode(route: Route): Int {
        return when (route.mode) {
            MoveType.WALK, MoveType.TRANSFER -> R.drawable.ic_walk_white
            MoveType.BUS -> R.drawable.ic_bus_white
            MoveType.SUBWAY -> R.drawable.ic_subway_white
            else -> R.drawable.ic_star_white
        }
    }

    private fun secondToHourAndMinute(second: Int): String {
        return "${second / 60 / 60}시간 ${second / 60 % 60}분"
    }
}