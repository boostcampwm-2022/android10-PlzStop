package com.stop.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.stop.model.route.Place

@BindingAdapter("set_place_name")
fun TextView.setPlaceName(place: Place?) {
    if (place == null) return

    if (place.name.isEmpty()) {
        text = place.roadAddress
        return
    }
    text = place.name
}