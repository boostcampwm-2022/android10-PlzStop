package com.stop.ui.nearplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.stop.BuildConfig
import com.stop.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceSearchFragment : Fragment() {

    private val placeSearchViewModel : PlaceSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeSearchViewModel.getNearPlaceList(
            1,
            "아남타워",
            126.96965F,
            37.55383F,
            BuildConfig.TMAP_APP_KEY
        )
    }

}