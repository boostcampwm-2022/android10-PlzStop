package com.stop.data.remote.source.nearplace

import com.stop.data.model.nearplace.Place
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.network.NearPlaceApiService
import javax.inject.Inject

internal class NearPlaceRemoteDataSourceImpl @Inject constructor(
    private val nearPlaceApiService: NearPlaceApiService
) : NearPlaceRemoteDataSource {

    override suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): List<Place> {
        val result = nearPlaceApiService.getNearPlaces(
            version,
            searchKeyword,
            centerLon,
            centerLat,
            appKey
        )

        when (result) {
            is NetworkResult.Success -> {
                return result.data.searchPoiInfo.pois.poi.map {
                    it.toRepositoryModel()
                }
            }
            is NetworkResult.Failure -> {
                throw Exception(result.message)
            }
            is NetworkResult.NetworkError -> {
                throw result.exception
            }
            is NetworkResult.Unexpected -> {
                throw result.exception
            }
        }
    }
}