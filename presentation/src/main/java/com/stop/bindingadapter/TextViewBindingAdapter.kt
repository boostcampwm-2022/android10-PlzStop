package com.stop.bindingadapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.stop.R
import com.stop.model.route.Place

@BindingAdapter("set_place_name", "set_text")
fun TextView.setPlaceName(place: Place?, newText: String) {
    if (place == null) {
        setTextColor(ContextCompat.getColor(context, R.color.main_light_grey))
        text = newText
        return
    }

    setTextColor(ContextCompat.getColor(context, R.color.main_dark_grey))

    if (place.name.isEmpty()) {
        text = place.roadAddress
        return
    }

    text = place.name
}