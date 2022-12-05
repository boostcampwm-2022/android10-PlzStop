package com.stop.data.repository

import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSource
import com.stop.domain.model.nearplace.PlaceUseCaseItem
import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem
import com.stop.domain.repository.NearPlaceRepository
import javax.inject.Inject

internal class NearPlaceRepositoryImpl @Inject constructor(
    private val nearPlaceRemoteDataSource: NearPlaceRemoteDataSource
) : NearPlaceRepository {

    override suspend fun getNearPlaces(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): List<PlaceUseCaseItem> {
        return nearPlaceRemoteDataSource.getNearPlaces(
            searchKeyword = searchKeyword,
            centerLon = centerLon,
            centerLat = centerLat
        ).onSuccess { places ->
            return places.map {
                it.toUseCaseModel()
            }
        }.onFailure {
            throw it
        }.getOrDefault(emptyList()) as List<PlaceUseCaseItem>
    }

    override suspend fun getNowStationLocationInfo(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): NowStationLocationUseCaseItem {
        return nearPlaceRemoteDataSource.getNearPlaces(
            searchKeyword = searchKeyword,
            centerLon = centerLon,
            centerLat = centerLat
        ).onSuccess { places ->
            return places.first().toNowStationLocationUseCaseModel()
        }.onFailure {
            throw it
        }.getOrDefault(NowStationLocationUseCaseItem("", "0", "0")) as NowStationLocationUseCaseItem
    }

}