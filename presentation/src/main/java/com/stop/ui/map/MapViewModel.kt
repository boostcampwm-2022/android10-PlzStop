package com.stop.ui.map

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.geoLocation.GeoLocationInfo
import com.stop.domain.usecase.geoLocation.GeoLocationUseCase
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geoLocationUseCase: GeoLocationUseCase
) : ViewModel() {

    private val _geoLocation = MutableLiveData<GeoLocationInfo>()
    val geoLocation: LiveData<GeoLocationInfo> = _geoLocation

    private val _panelVisibility = MutableLiveData(View.GONE)
    val panelVisibility: LiveData<Int> = _panelVisibility

    fun getGeoLocationInfo(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _geoLocation.value = geoLocationUseCase.getGeoLocationInfo(latitude, longitude)
            _panelVisibility.value = View.VISIBLE
//            getDistance(latitude, longitude)
        }
    }

}