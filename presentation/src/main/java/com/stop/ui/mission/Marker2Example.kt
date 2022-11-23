package com.stop.ui.mission

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.stop.R

class Marker2Example(context: Context?) : LinearLayout(context) {
    private val str1TextView: TextView
    private val str2TextView: TextView

    init {
        inflate(context, R.layout.view_marker2, this)
        str1TextView = findViewById(R.id.marker2_text1)
        str2TextView = findViewById(R.id.marker2_text2)
    }

    fun setText(str1: String?, str2: String?) {
        str1TextView.text = str1
        str2TextView.text = str2
    }
}