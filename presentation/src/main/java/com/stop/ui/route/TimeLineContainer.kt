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
import androidx.core.view.doOnLayout
import com.stop.R
import com.stop.databinding.TimeLineItemBinding
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.Route
import com.stop.domain.model.route.tmap.custom.TransportRoute

class TimeLineContainer(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val greyColor =
        ContextCompat.getColor(context, R.color.grey_for_route_walk)
    private var beforeViewId: Int? = null
    private var beforeView: View? = null

    private var iconWidth: Int? = null
    private var iconCount: Int? = null
    private var textWidth: Int? = null
    private var routeCount: Int? = null
    private var overlappingWidth: Int? = null

    private val density = context.resources.displayMetrics.density

    fun submitList(routes: List<Route>) {
        var count = 0
        routes.forEachIndexed { index, route ->
            if (index == 0) {
                return@forEachIndexed
            }
            if (route.mode in listOf(MoveType.WALK, MoveType.TRANSFER)) {
                return@forEachIndexed
            }
            count += 1
        }

        iconWidth = convertDpToPixel(9f)
        iconCount = count + 1

        textWidth = convertDpToPixel(30f)
        routeCount = routes.size

        overlappingWidth = OVERLAPPING_MARGIN * (routes.size - 1)

        doOnLayout {
            routes.forEachIndexed { index, route ->
                val timeLineItem2Binding = TimeLineItemBinding.inflate(
                    LayoutInflater.from(context),
                    this@TimeLineContainer,
                    true,
                ).apply {
                    root.id = View.generateViewId()
                    if (index != 0) {
                        val layoutParams = root.layoutParams as MarginLayoutParams
                        layoutParams.marginStart = -OVERLAPPING_MARGIN
                        root.requestLayout()
                        root.layoutParams = layoutParams
                    }
                }
                setBindingAttribute(timeLineItem2Binding, route, index)
                beforeView = timeLineItem2Binding.root
            }
        }
    }

    private fun convertDpToPixel(size: Float): Int {
        return (size * density + 0.5f).toInt()
    }

    private fun setBindingAttribute(binding: TimeLineItemBinding, route: Route, index: Int) {
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
                    setWidth(binding, route.proportionOfSectionTime)
                    setConstraint(binding)

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

        setWidth(binding, route.proportionOfSectionTime)
        setConstraint(binding)
    }

    private fun setConstraint(binding: TimeLineItemBinding) {
        val endId = beforeViewId ?: this@TimeLineContainer.id
        val endSide = if (beforeViewId == null) {
            ConstraintSet.START
        } else {
            ConstraintSet.END
        }

        with(ConstraintSet()) {
            clone(this@TimeLineContainer)
            connect(binding.root.id, ConstraintSet.START, endId, endSide)
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

    private fun setWidth(
        binding: TimeLineItemBinding,
        proportionOfSectionTime: Float
    ) {
        val iconWidth = iconWidth ?: throw IllegalArgumentException("로직이 잘못 되었습니다.")
        val iconCount = iconCount ?: throw IllegalArgumentException("로직이 잘못 되었습니다.")
        val textWidth = textWidth ?: throw IllegalArgumentException("로직이 잘못 되었습니다.")
        val routeCount = routeCount ?: throw IllegalArgumentException("로직이 잘못 되었습니다.")
        val overlappingWidth = overlappingWidth ?: throw IllegalArgumentException("로직이 잘못 되었습니다.")
        val extraWidth =
            this@TimeLineContainer.width - iconWidth * iconCount - textWidth * routeCount + overlappingWidth
        binding.root.layoutParams.width =
            (extraWidth * proportionOfSectionTime).toInt() + iconWidth + textWidth
    }

    private fun setIdentityColor(binding: TimeLineItemBinding, route: TransportRoute) {
        val identityColor = Color.parseColor("#${route.routeColor}")
        binding.textViewSectionTime.background.setTint(identityColor)
        binding.textViewSectionTime.setTextColor(Color.WHITE)

        binding.viewIcon.background.setTint(identityColor)
        binding.imageViewIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
    }

    private fun setDefaultColor(binding: TimeLineItemBinding) {
        binding.textViewSectionTime.setTextColor(Color.WHITE)
        binding.textViewSectionTime.background.setTint(greyColor)

        binding.viewIcon.background.setTintList(null)
    }

    companion object {
        private const val OVERLAPPING_MARGIN = 30
    }
}