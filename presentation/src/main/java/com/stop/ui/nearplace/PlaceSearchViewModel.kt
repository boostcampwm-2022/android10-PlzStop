package com.stop.ui.nearplace

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.nearplace.Place
import com.stop.domain.usecase.nearplace.GetNearPlaceListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val getNearPlaceListUseCase: GetNearPlaceListUseCase
) : ViewModel() {

    private val _nearPlaceList = MutableLiveData<List<Place>>()
    val nearPlaceList: LiveData<List<Place>> = _nearPlaceList

    fun getNearPlaceList(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ) {
        viewModelScope.launch {
            try {
                _nearPlaceList.postValue(
                    getNearPlaceListUseCase.getNearPlaceList(
                        version,
                        searchKeyword,
                        centerLon,
                        centerLat,
                        appKey
                    )
                )
            } catch (e: Exception) {
                Log.e("ABC", e.message.toString())
            }
        }
    }

}