package com.stop.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.stop.domain.model.nearplace.RoadAddress

@BindingAdapter("roadName")
fun TextView.setRoadName(roadAddressList: List<RoadAddress>?) {
    if (roadAddressList.isNullOrEmpty()) {
        this.text = ""
    } else {
        this.text = roadAddressList[0].fullAddressRoad
    }
}