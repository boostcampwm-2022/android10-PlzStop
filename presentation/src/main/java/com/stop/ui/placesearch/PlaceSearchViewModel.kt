package com.stop.ui.placesearch

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val getNearPlacesUseCase: GetNearPlacesUseCase
) : ViewModel() {

    var currentLocation = Location(0.0, 0.0)

    private val _nearPlaceList = MutableStateFlow<List<Place>>(emptyList())
    val nearPlaceList: StateFlow<List<Place>> = _nearPlaceList

    private val errorMessageChannel = Channel<String>()
    val errorMessage = errorMessageChannel.receiveAsFlow()

    private val _clickPlace = MutableLiveData<Event<Place>>()
    val clickPlace: LiveData<Event<Place>> = _clickPlace

    private val clickCurrentLocationChannel = Channel<Boolean>()
    val clickCurrentLocation = clickCurrentLocationChannel.receiveAsFlow()

    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword : StateFlow<String> = _searchKeyword

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
                        TMAP_VERSION,
                        searchKeyword,
                        currentLocation.longitude,
                        currentLocation.latitude,
                        BuildConfig.TMAP_APP_KEY
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

    companion object {
        private const val TMAP_VERSION = 1
    }

}