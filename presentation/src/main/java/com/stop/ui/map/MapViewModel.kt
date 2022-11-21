package com.stop.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stop.domain.model.geoLocation.GeoLocationInfo
import com.stop.domain.usecase.geoLocation.GeoLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geoLocationUseCase: GeoLocationUseCase
) : ViewModel() {

    private val _geoLocation = MutableLiveData<GeoLocationInfo>()
    val geoLocation: LiveData<GeoLocationInfo> = _geoLocation

    fun getGeoLocationInfo() {
        geoLocationUseCase.getGeoLocationInfo(

        )
    }
}