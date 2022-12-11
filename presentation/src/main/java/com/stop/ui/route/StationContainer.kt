package com.stop.ui.route

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.stop.databinding.RoutePathAtTimeLineItemBinding
import com.stop.model.route.RouteItem

class StationContainer(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private var beforeViewId: Int? = null

    fun submitList(routeItems: List<RouteItem>) {
        removeAllViews()

        routeItems.forEachIndexed { index, routeItem ->
            val routePathAtTimeLineItemBinding = RoutePathAtTimeLineItemBinding.inflate(
                LayoutInflater.from(context),
                this,
                true,
            ).apply {
                root.id = View.generateViewId()
            }

            setBindingAttribute(routePathAtTimeLineItemBinding, routeItem, index)
            setConstraint(routePathAtTimeLineItemBinding)
        }
    }

    private fun setBindingAttribute(
        binding: RoutePathAtTimeLineItemBinding,
        routeItem: RouteItem,
        index: Int
    ) {
        binding.routeItem = routeItem
        binding.executePendingBindings()

        if (index == 0) {
            binding.viewBeforeLine.visibility = View.INVISIBLE
        }
    }

    private fun setConstraint(binding: RoutePathAtTimeLineItemBinding) {
        val endId = beforeViewId ?: this.id
        val endSide = if (beforeViewId == null) {
            ConstraintSet.TOP
        } else {
            ConstraintSet.BOTTOM
        }

        with(ConstraintSet()) {
            clone(this@StationContainer)
            connect(binding.root.id, ConstraintSet.TOP, endId, endSide)
            connect(
                binding.root.id,
                ConstraintSet.START,
                this@StationContainer.id,
                ConstraintSet.START
            )
            connect(
                binding.root.id,
                ConstraintSet.END,
                this@StationContainer.id,
                ConstraintSet.END
            )
            applyTo(this@StationContainer)
        }
        beforeViewId = binding.root.id
    }
}