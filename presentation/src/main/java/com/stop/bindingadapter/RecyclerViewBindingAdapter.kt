package com.stop.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.domain.model.nearplace.Place
import com.stop.ui.nearplace.NearPlaceAdapter

@BindingAdapter("places")
fun RecyclerView.setPlaces(places: List<Place>?) {
    if (this.adapter == null) {
        val adapter = NearPlaceAdapter()
        this.adapter = adapter
    }

    val nearPlaceAdapter = this.adapter as NearPlaceAdapter
    nearPlaceAdapter.submitList(places)
}
