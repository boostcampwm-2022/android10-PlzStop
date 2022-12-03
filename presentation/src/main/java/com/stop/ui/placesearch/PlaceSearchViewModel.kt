package com.stop.ui.placesearch

import android.text.Editable
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.geoLocation.GeoLocationInfo
import com.stop.domain.model.nearplace.Place
import com.stop.domain.usecase.geoLocation.GeoLocationUseCase
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCase
import com.stop.model.Event
import com.stop.model.Location
import com.stop.model.route.Coordinate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val getNearPlacesUseCase: GetNearPlacesUseCase,
    private val geoLocationUseCase: GeoLocationUseCase
) : ViewModel() {

    var currentLocation = Location(0.0, 0.0)

    var panelInfo: com.stop.model.route.Place? = null

    private val _nearPlaceList = MutableStateFlow<List<Place>>(emptyList())
    val nearPlaceList: StateFlow<List<Place>> = _nearPlaceList

    var bookmarks = mutableListOf(EXAMPLE_BOOKMARK_1, EXAMPLE_BOOKMARK_2, EXAMPLE_BOOKMARK_3)

    private val errorMessageChannel = Channel<String>()
    val errorMessage = errorMessageChannel.receiveAsFlow()

    private val _clickPlace = MutableLiveData<Event<Place>>()
    val clickPlace: LiveData<Event<Place>> = _clickPlace

    private val clickCurrentLocationChannel = Channel<Boolean>()
    val clickCurrentLocation = clickCurrentLocationChannel.receiveAsFlow()

    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword : StateFlow<String> = _searchKeyword

    private val _geoLocation = MutableLiveData<GeoLocationInfo>()
    val geoLocation: LiveData<GeoLocationInfo> = _geoLocation

    private val _panelVisibility = MutableLiveData(View.GONE)
    val panelVisibility: LiveData<Int> = _panelVisibility

    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float> = _distance

    fun afterTextChanged(s: Editable?) {
        _searchKeyword.value = s.toString()
    }

    fun getNearPlaces(
        searchKeyword: String,
    ) {
        viewModelScope.launch {
            try {
                _nearPlaceList.emit(
                    getNearPlacesUseCase.getNearPlaces(
                        searchKeyword,
                        currentLocation.longitude,
                        currentLocation.latitude
                    )
                )
            } catch (e: Exception) {
                setNearPlaceListEmpty()
                errorMessageChannel.send(e.message ?: "something is wrong")
            }
        }
    }

    fun setNearPlaceListEmpty() {
        _nearPlaceList.value = emptyList()
    }

    fun setClickPlace(place: Place) {
        _clickPlace.value = Event(place)
    }

    fun setClickCurrentLocation() {
        viewModelScope.launch {
            clickCurrentLocationChannel.send(true)
        }
    }

    fun getGeoLocationInfo(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _geoLocation.value = geoLocationUseCase.getGeoLocationInfo(latitude, longitude)

            readySendValue(latitude, longitude)
            _panelVisibility.value = View.VISIBLE
            getDistance(latitude, longitude)
        }
    }

    private fun readySendValue(latitude: Double, longitude: Double) {
        val clickedValue = _geoLocation.value ?: return

        panelInfo = com.stop.model.route.Place(
            clickedValue.title,
            Coordinate(latitude.toString(), longitude.toString()),
            clickedValue.roadAddress,
        )
    }

    private fun getDistance(latitude: Double, longitude: Double) {
        val startPoint = android.location.Location("Start")
        val endPoint = android.location.Location("End")

        startPoint.latitude = latitude
        startPoint.longitude = longitude
        endPoint.latitude = currentLocation.latitude
        endPoint.longitude = currentLocation.longitude
        _distance.value = round(startPoint.distanceTo(endPoint) / 100) / 10
    }

    companion object {
        private val EXAMPLE_BOOKMARK_1 = Location(37.3931010, 126.9781449)
        private val EXAMPLE_BOOKMARK_2 = Location(37.55063543842469, 127.07369927986392)
        private val EXAMPLE_BOOKMARK_3 = Location(37.48450549635376, 126.89324337770405)
    }

}