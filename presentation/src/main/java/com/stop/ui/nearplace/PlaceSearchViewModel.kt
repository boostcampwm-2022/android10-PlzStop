package com.stop.ui.nearplace

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.BuildConfig
import com.stop.domain.model.nearplace.Place
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCase
import com.stop.model.Event
import com.stop.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val getNearPlacesUseCase: GetNearPlacesUseCase
) : ViewModel() {

    var currentLocation = Location(0.0, 0.0)

    var bookmarks = mutableListOf(EXAMPLE_BOOKMARK_1, EXAMPLE_BOOKMARK_2, EXAMPLE_BOOKMARK_3)

    private val _nearPlaceList = MutableLiveData<List<Place>>()
    val nearPlaceList: LiveData<List<Place>> = _nearPlaceList

    private val errorMessageChannel = Channel<String>()
    val errorMessage = errorMessageChannel.receiveAsFlow()

    private val _clickPlace = MutableLiveData<Event<Place>>()
    val clickPlace: LiveData<Event<Place>> = _clickPlace

    private val clickCurrentLocationChannel = Channel<Boolean>()
    val clickCurrentLocation = clickCurrentLocationChannel.receiveAsFlow()

    fun afterTextChanged(s: Editable?) {
        getNearPlaces(
            s.toString(),
            currentLocation.longitude,
            currentLocation.latitude
        )

        if (s.toString().isBlank()) {
            _nearPlaceList.postValue(emptyList())
        }
    }

    private fun getNearPlaces(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ) {
        viewModelScope.launch {
            try {
                getNearPlacesUseCase.getNearPlaces(
                    TMAP_VERSION,
                    searchKeyword,
                    centerLon,
                    centerLat,
                    BuildConfig.TMAP_APP_KEY
                ).collectLatest {
                    _nearPlaceList.postValue(it)
                }
            } catch (e: Exception) {
                setNearPlaceListEmpty()
                errorMessageChannel.send(e.message ?: "something is wrong")
            }
        }
    }

    fun setNearPlaceListEmpty() {
        _nearPlaceList.postValue(emptyList())
    }

    fun setClickPlace(place: Place) {
        _clickPlace.value = Event(place)
    }

    fun setClickCurrentLocation() {
        viewModelScope.launch {
            clickCurrentLocationChannel.send(true)
        }
    }

    companion object {
        private const val TMAP_VERSION = 1
        private val EXAMPLE_BOOKMARK_1 = Location(37.3931010, 126.9781449)
        private val EXAMPLE_BOOKMARK_2 = Location(37.55063543842469, 127.07369927986392)
        private val EXAMPLE_BOOKMARK_3 = Location(37.48450549635376, 126.89324337770405)
    }


}