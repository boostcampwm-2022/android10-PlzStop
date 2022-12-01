package com.stop.data.remote.source.nearplace

import com.stop.data.model.nearplace.Place
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.network.TmapApiService
import javax.inject.Inject

internal class NearPlaceRemoteDataSourceImpl @Inject constructor(
    private val tmapApiService: TmapApiService
) : NearPlaceRemoteDataSource {

    override suspend fun getNearPlaces(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): Result<List<Place>> {
        val result = tmapApiService.getNearPlaces(
            searchKeyword = searchKeyword,
            centerLon = centerLon,
            centerLat = centerLat
        )
        return runCatching {
            when (result) {
                is NetworkResult.Failure -> {
                    throw Exception(result.message)
                }
                is NetworkResult.Success -> {
                    result.data.searchPoiInfo.pois.poi.map {
                        it.toRepositoryModel()
                    }
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
}