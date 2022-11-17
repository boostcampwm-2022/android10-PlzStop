package com.stop.ui.route

import androidx.recyclerview.widget.RecyclerView
import com.stop.databinding.RouteItemBinding
import com.stop.domain.model.Itinerary
import com.stop.domain.model.MoveType
import com.stop.domain.model.PublicTransportRoute
import com.stop.util.TimeCalculator

class RouteViewHolder(
    private val binding: RouteItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(itinerary: Itinerary) {
        binding.itinerary = itinerary
        binding.textViewExpectedRoute.text = calculateExpectedRoute(itinerary)
        binding.textViewExpectedRequiredTime.text =
            TimeCalculator.secondToHourAndMinute(itinerary.totalTime)
    }

    private fun calculateExpectedRoute(itinerary: Itinerary): String {
        return itinerary.routes.joinToString(" -> ") { route ->
            when (route) {
                is PublicTransportRoute -> {
                    when (route.mode) {
                        MoveType.SUBWAY -> "${route.routeInfo} ${route.start.name}역"
                        MoveType.BUS -> {
                            val (busType, busNum) = route.routeInfo.split(":")
                            "${route.start.name} 정거장 $busType $busNum 버스"
                        }
                        else -> route.start.name
                    }
                }
                else -> route.start.name
            }
        } + " -> 도착지"
    }
}