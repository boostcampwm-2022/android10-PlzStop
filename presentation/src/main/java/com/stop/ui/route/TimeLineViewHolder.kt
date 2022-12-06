package com.stop.ui.route

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stop.R
import com.stop.databinding.TimeLineItemBinding
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.Route
import com.stop.domain.model.route.tmap.custom.TransportRoute

class TimeLineViewHolder(
    private val binding: TimeLineItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val greyColor =
        ContextCompat.getColor(binding.root.context, R.color.grey_for_route_walk)

    fun bind(route: Route, position: Int) {
        val text = binding.root.resources.getString(
            R.string.section_time,
            (route.sectionTime / 60).toInt().toString()
        )
        binding.textViewSectionTime.text = text

        val imageSrc = when (route.mode) {
            MoveType.BUS -> R.drawable.time_line_bus_16
            MoveType.SUBWAY -> R.drawable.time_line_subway_16
            MoveType.WALK, MoveType.TRANSFER -> {
                if (position != 0) {
                    setDefaultColor()
                    binding.viewIcon.visibility = View.GONE
                    binding.imageViewIcon.visibility = View.GONE
                    return
                }
                R.drawable.time_line_directions_walk_16
            }
            else -> R.drawable.time_line_help_16
        }
        binding.viewIcon.visibility = View.VISIBLE
        binding.imageViewIcon.visibility = View.VISIBLE

        val drawable = ContextCompat.getDrawable(binding.root.context, imageSrc)
            ?: throw IllegalArgumentException()

        binding.imageViewIcon.setImageDrawable(drawable)

        when (route) {
            is TransportRoute -> setIdentityColor(route)
            else -> setDefaultColor()
        }
    }

    private fun setIdentityColor(route: TransportRoute) {
        val identityColor = Color.parseColor("#${route.routeColor}")
        binding.textViewSectionTime.setBackgroundColor(identityColor)
        binding.textViewSectionTime.setTextColor(Color.WHITE)
        binding.viewIcon.background.setTint(identityColor)
        binding.imageViewIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
    }

    private fun setDefaultColor() {
        binding.textViewSectionTime.setTextColor(Color.WHITE)
        binding.textViewSectionTime.setBackgroundColor(greyColor)

        val drawable =
            ContextCompat.getDrawable(binding.root.context, R.drawable.time_stick_round_background)
                ?: throw IllegalArgumentException()
        binding.viewIcon.background = drawable
        binding.viewIcon.background.setTintList(null)
    }
}