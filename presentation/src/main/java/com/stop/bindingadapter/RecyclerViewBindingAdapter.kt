package com.stop.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("items")
fun <T> RecyclerView.submitItems(items: List<T>?) {
    if (this.adapter is ListAdapter<*, *>) {
        (this.adapter as ListAdapter<T, *>).submitList(items)
    }
}
