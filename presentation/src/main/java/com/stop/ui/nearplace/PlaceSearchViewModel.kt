package com.stop.ui.nearplace

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.BuildConfig
import com.stop.domain.model.nearplace.Place
import com.stop.domain.usecase.nearplace.GetNearPlaceListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val getNearPlaceListUseCase: GetNearPlaceListUseCase
) : ViewModel() {

    private val _nearPlaceList = MutableLiveData<List<Place>>()
    val nearPlaceList: LiveData<List<Place>> = _nearPlaceList

    private val eventChannel = Channel<String>()
    val errorMessage = eventChannel.receiveAsFlow()

    fun afterTextChanged(s: Editable?) {
        if(s.toString().isBlank()){
            _nearPlaceList.postValue(emptyList())
        }

        getNearPlaceList(
            s.toString(),
            126.96965F,
            37.55383F
        )
    }

    private fun getNearPlaceList(
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float
    ) {
        viewModelScope.launch {
            try {
                _nearPlaceList.postValue(
                    getNearPlaceListUseCase.getNearPlaceList(
                        TMAP_VERSION,
                        searchKeyword,
                        centerLon,
                        centerLat,
                        BuildConfig.TMAP_APP_KEY
                    )
                )
            } catch (e: Exception) {
                _nearPlaceList.postValue(emptyList())
                eventChannel.send(e.message ?: "something is wrong")
            }
        }
    }

    companion object {
        private const val TMAP_VERSION = 1
    }

}