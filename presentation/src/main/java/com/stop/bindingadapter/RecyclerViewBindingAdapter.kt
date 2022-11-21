package com.stop.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stop.domain.model.nearplace.Place
import com.stop.ui.placesearch.NearPlaceAdapter

@BindingAdapter("places")
fun RecyclerView.setPlaces(places: List<Place>?) {
    this.adapter?.run {
        if(this is NearPlaceAdapter){
            this.submitList(places)
        }
    }
}
