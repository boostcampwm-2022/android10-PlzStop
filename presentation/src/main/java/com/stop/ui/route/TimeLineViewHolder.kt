package com.stop.ui.route

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stop.R
import com.stop.databinding.TimeLineItemBinding
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.Route

class TimeLineViewHolder(
    private val binding: TimeLineItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(route: Route, position: Int) {
        val text = route.start.name + "   " +
                binding.root.resources.getString(
                    R.string.section_time,
                    (route.sectionTime / 60).toInt().toString()
                )
        binding.textViewSectionTime.text = text


        val imageSrc = when (route.mode) {
            MoveType.BUS -> R.drawable.time_line_bus_16
            MoveType.SUBWAY -> R.drawable.time_line_subway_16
            MoveType.WALK, MoveType.TRANSFER -> {
                if (position != 0) {
                    binding.imageViewIcon.visibility = View.GONE
                    return
                }
                R.drawable.time_line_directions_walk_16
            }
            else -> R.drawable.time_line_help_16
        }
        binding.imageViewIcon.visibility = View.VISIBLE

        val drawable = ContextCompat.getDrawable(binding.root.context, imageSrc)
            ?: throw IllegalArgumentException()

        binding.imageViewIcon.setImageDrawable(drawable)
    }
}