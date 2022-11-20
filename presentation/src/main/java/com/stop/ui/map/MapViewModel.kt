package com.stop.ui.map

import androidx.lifecycle.ViewModel
import com.stop.domain.usecase.geoLocation.GeoLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geoLocationUseCase: GeoLocationUseCase
) : ViewModel(){

}