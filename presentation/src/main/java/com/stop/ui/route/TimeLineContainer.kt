package com.stop.ui.route

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.stop.R
import com.stop.databinding.ItemTimeLineBinding
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.Route
import com.stop.domain.model.route.tmap.custom.TransportRoute
import kotlin.properties.Delegates

class TimeLineContainer(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val greyColor = ContextCompat.getColor(context, R.color.grey_for_route_walk)
    private val density = context.resources.displayMetrics.density

    private var beforeViewId: Int? = null
    private var beforeView: View? = null
    private var routeCount by Delegates.notNull<Int>()
    private var overlappingWidth by Delegates.notNull<Int>()

    fun submitList(routes: List<Route>) {
        clearBeforeData()

        routeCount = routes.size
        val overlappingMarginPixel = (OVERLAPPING_MARGIN * density + 0.5f).toInt()
        overlappingWidth = overlappingMarginPixel * (routes.size - 1)

        routes.forEachIndexed { index, route ->
            val timeLineItem2Binding = ItemTimeLineBinding.inflate(
                LayoutInflater.from(context),
                this@TimeLineContainer,
                true,
            ).apply {
                root.id = View.generateViewId()
                if (index != 0) {
                    val layoutParams = root.layoutParams as MarginLayoutParams
                    layoutParams.marginStart = -overlappingMarginPixel
                    root.requestLayout()
                    root.layoutParams = layoutParams
                }
            }
            setBindingAttribute(timeLineItem2Binding, route, index)
            beforeView = timeLineItem2Binding.root
        }
    }

    private fun clearBeforeData() {
        beforeViewId = null
        beforeView = null
    }

    private fun setBindingAttribute(binding: ItemTimeLineBinding, route: Route, index: Int) {
        val filterTime = if (routeCount < 7) {
            60.0
        } else {
            180.0
        }
        val correctionValue =
            if (route.sectionTime <= filterTime) {
                if (route.mode == MoveType.TRANSFER) {
                    0.05f
                } else {
                    0.25f
                }
            } else {
                val text = binding.root.resources.getString(
                    R.string.section_time,
                    (route.sectionTime / 60).toInt().toString()
                )
                binding.textViewSectionTime.text = text
                0.25f
            }

        val imageSrc = when (route.mode) {
            MoveType.BUS -> R.drawable.time_line_bus_16
            MoveType.SUBWAY -> R.drawable.time_line_subway_16
            MoveType.WALK, MoveType.TRANSFER -> {
                if (index != 0) {
                    setDefaultColor(binding)
                    binding.viewIcon.visibility = View.GONE
                    binding.imageViewIcon.visibility = View.GONE
                    setConstraint(binding, index, route.proportionOfSectionTime, correctionValue)

                    beforeView?.bringToFront()
                    requestLayout()
                    invalidate()
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

        setConstraint(binding, index, route.proportionOfSectionTime, correctionValue)
    }

    private fun setConstraint(
        binding: ItemTimeLineBinding,
        index: Int,
        proportionOfSectionTime: Float,
        correctionValue: Float,
    ) {
        val endId = beforeViewId ?: this@TimeLineContainer.id
        val endSide = if (beforeViewId == null) {
            ConstraintSet.START
        } else {
            ConstraintSet.END
        }

        with(ConstraintSet()) {
            clone(this@TimeLineContainer)
            connect(binding.root.id, ConstraintSet.START, endId, endSide)
            if (index != 0) {
                connect(
                    endId,
                    ConstraintSet.END,
                    binding.root.id,
                    ConstraintSet.START
                )
            }
            setHorizontalWeight(binding.root.id, proportionOfSectionTime + correctionValue)

            if (index == routeCount - 1) {
                connect(
                    binding.root.id,
                    ConstraintSet.END,
                    this@TimeLineContainer.id,
                    ConstraintSet.END
                )
                setHorizontalChainStyle(binding.root.id, ConstraintSet.CHAIN_SPREAD_INSIDE)
            }
            connect(
                binding.root.id,
                ConstraintSet.TOP,
                this@TimeLineContainer.id,
                ConstraintSet.TOP
            )
            connect(
                binding.root.id,
                ConstraintSet.BOTTOM,
                this@TimeLineContainer.id,
                ConstraintSet.BOTTOM
            )
            applyTo(this@TimeLineContainer)
        }
        beforeViewId = binding.root.id
    }

    private fun setIdentityColor(binding: ItemTimeLineBinding, route: TransportRoute) {
        val identityColor = Color.parseColor("#${route.routeColor}")

        binding.textViewSectionTime.background.setTint(identityColor)
        binding.textViewSectionTime.setTextColor(Color.WHITE)

        binding.viewIcon.background.setTint(identityColor)
        binding.imageViewIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
    }

    private fun setDefaultColor(binding: ItemTimeLineBinding) {
        binding.textViewSectionTime.background.setTint(greyColor)
        binding.textViewSectionTime.setTextColor(Color.WHITE)

        binding.viewIcon.background.setTint(greyColor)
        binding.imageViewIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
    }

    companion object {
        private const val OVERLAPPING_MARGIN = 10f
    }

}