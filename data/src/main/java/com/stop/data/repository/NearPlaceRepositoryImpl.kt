package com.stop.data.repository

import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSource
import com.stop.domain.model.nearplace.Place
import com.stop.domain.repository.NearPlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    ): Flow<List<Place>> = flow {
        emit(
            nearPlaceRemoteDataSource.getNearPlaces(
                version,
                searchKeyword,
                centerLon,
                centerLat,
                appKey
            ).map {
                it.toUseCaseModel()
            }
        )
    }

}