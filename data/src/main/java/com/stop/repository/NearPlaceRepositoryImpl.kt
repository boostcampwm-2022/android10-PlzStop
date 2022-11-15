package com.stop.repository

import com.stop.model.nearplace.Place
import com.stop.remote.model.NetworkResult
import com.stop.remote.source.nearplace.NearPlaceRemoteDataSource
import javax.inject.Inject

internal class NearPlaceRepositoryImpl @Inject constructor(
    private val nearPlaceRemoteDataSource: NearPlaceRemoteDataSource
) : NearPlaceRepository{

    override suspend fun getNearPlaceList(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ): List<Place> {
        val result = nearPlaceRemoteDataSource.getNearPlaceList(
            version,
            searchKeyword,
            centerLon,
            centerLat,
            appKey
        )

        when(result){
            is NetworkResult.Error -> {
                throw Exception(result.message)
            }
            is NetworkResult.Success -> {
                return result.data?.searchPoiInfo?.pois?.poi?.map{
                    it.toRepositoryModel()
                } ?: emptyList()
            }
        }
    }

}