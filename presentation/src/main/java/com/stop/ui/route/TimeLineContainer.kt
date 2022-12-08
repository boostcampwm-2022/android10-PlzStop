package com.stop.ui.route

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.stop.R
import com.stop.databinding.TimeLineItem2Binding
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.Route
import com.stop.domain.model.route.tmap.custom.TransportRoute
import kotlinx.coroutines.CoroutineScope

class TimeLineContainer(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val greyColor =
        ContextCompat.getColor(context, R.color.grey_for_route_walk)
    private var beforeViewIconId: Int? = null

    fun submitList(routes: List<Route>) {
        routes.forEachIndexed { index, route ->

            val timeLineItem2Binding = TimeLineItem2Binding.inflate(
                LayoutInflater.from(context),
                this,
                false,
            ).apply {
                root.id = View.generateViewId()
            }
//            this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            addView(timeLineItem2Binding.root)
            setBindingAttribute(timeLineItem2Binding, route, index)
            TransitionManager.beginDelayedTransition(this@TimeLineContainer)
        }
    }

    private fun setBindingAttribute(binding: TimeLineItem2Binding, route: Route, index: Int) {

        val text = binding.root.resources.getString(
            R.string.section_time,
            (route.sectionTime / 60).toInt().toString()
        )
        binding.textViewSectionTime.text = text

        val imageSrc = when (route.mode) {
            MoveType.BUS -> R.drawable.time_line_bus_16
            MoveType.SUBWAY -> R.drawable.time_line_subway_16
            MoveType.WALK, MoveType.TRANSFER -> {
                if (index != 0) {
                    setDefaultColor(binding)
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
            is TransportRoute -> setIdentityColor(binding, route)
            else -> setDefaultColor(binding)
        }

        setWidth(binding, route.proportionOfSectionTime)
        setConstraint(binding)
    }

    // viewIcon 위주로 설정하면 됨
    private fun setConstraint(binding: TimeLineItem2Binding) {
        val endId = beforeViewIconId ?: this@TimeLineContainer.id
        val endSide = if (beforeViewIconId == null) {
            ConstraintSet.START
        } else {
            ConstraintSet.END
        }
        TransitionManager.beginDelayedTransition(this@TimeLineContainer)

        with(ConstraintSet()) {
            clone(this@TimeLineContainer)
            connect(binding.root.id, ConstraintSet.START, endId, endSide)
            connect(binding.root.id, ConstraintSet.TOP, this@TimeLineContainer.id, ConstraintSet.TOP)
            connect(binding.root.id, ConstraintSet.BOTTOM, this@TimeLineContainer.id, ConstraintSet.BOTTOM)
            applyTo(this@TimeLineContainer)
        }
        beforeViewIconId = binding.root.id
    }

    private fun setWidth(
        binding: TimeLineItem2Binding,
        proportionOfSectionTime: Float
    ) {
        this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

        val layoutParams = binding.root.layoutParams
        layoutParams.width = (this.measuredWidth * proportionOfSectionTime).toInt()
    }

    private fun setIdentityColor(binding: TimeLineItem2Binding, route: TransportRoute) {
        val identityColor = Color.parseColor("#${route.routeColor}")
        binding.textViewSectionTime.setBackgroundColor(identityColor)
        binding.textViewSectionTime.setTextColor(Color.WHITE)
        binding.viewIcon.background.setTint(identityColor)
        binding.imageViewIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
    }

    private fun setDefaultColor(binding: TimeLineItem2Binding) {
        binding.textViewSectionTime.setTextColor(Color.WHITE)
        binding.textViewSectionTime.setBackgroundColor(greyColor)

        val drawable =
            ContextCompat.getDrawable(binding.root.context, R.drawable.time_stick_round_background)
                ?: throw IllegalArgumentException()
        binding.viewIcon.background = drawable
        binding.viewIcon.background.setTintList(null)
    }
}