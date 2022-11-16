package com.stop.ui.nearplace

import androidx.lifecycle.*
import com.stop.BuildConfig
import com.stop.domain.model.nearplace.Place
import com.stop.domain.usecase.nearplace.GetNearPlaceListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val getNearPlaceListUseCase: GetNearPlaceListUseCase
) : ViewModel() {

    val searchKeyword = MutableLiveData("")

    val nearPlaceList: LiveData<List<Place>> = Transformations.map(searchKeyword) { query ->
        if (query.isNullOrBlank()) {
            emptyList()
        } else {
            getNearPlaceList(
                query,
                126.96965F,
                37.55383F,
                BuildConfig.TMAP_APP_KEY
            )
        }
    }

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private var result: List<Place>? = null
    private fun getNearPlaceList(
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ): List<Place> {
        viewModelScope.launch {
            try {
                result = getNearPlaceListUseCase.getNearPlaceList(
                    TMAP_VERSION,
                    searchKeyword,
                    centerLon,
                    centerLat,
                    appKey
                )
            } catch (e: Exception) {
                result = emptyList()
                _errorMessage.value = e.message ?: "something is wrong"
            }
        }
        return result ?: emptyList()
    }

    companion object {
        private const val TMAP_VERSION = 1
    }

}