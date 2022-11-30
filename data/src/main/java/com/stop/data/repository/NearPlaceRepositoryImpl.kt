package com.stop.data.repository

import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSource
import com.stop.domain.model.nearplace.Place
import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem
import com.stop.domain.repository.NearPlaceRepository
import javax.inject.Inject

internal class NearPlaceRepositoryImpl @Inject constructor(
    private val nearPlaceRemoteDataSource: NearPlaceRemoteDataSource
) : NearPlaceRepository {

    override suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): List<Place> {
        return nearPlaceRemoteDataSource.getNearPlaces(
            version,
            searchKeyword,
            centerLon,
            centerLat,
            appKey
        ).onSuccess { places ->
            return places.map {
                it.toUseCaseModel()
            }
        }.onFailure {
            throw it
        }.getOrDefault(emptyList()) as List<Place>
    }

    override suspend fun getNowStationLocationInfo(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): NowStationLocationUseCaseItem {
        return nearPlaceRemoteDataSource.getNearPlaces(
            version,
            searchKeyword,
            centerLon,
            centerLat,
            appKey
        ).onSuccess { places ->
            return places.first().toNowStationLocationUseCaseModel()
        }.onFailure {
            throw it
        }.getOrDefault(NowStationLocationUseCaseItem("", "0", "0")) as NowStationLocationUseCaseItem
    }

}