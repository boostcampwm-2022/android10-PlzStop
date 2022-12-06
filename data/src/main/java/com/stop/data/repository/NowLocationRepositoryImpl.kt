package com.stop.data.repository

import com.stop.domain.model.nowlocation.BusCurrentInformation
import com.stop.data.remote.source.nowlocation.NowLocationRemoteDataSource
import com.stop.domain.model.nowlocation.TrainLocationInfoDomain
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class NowLocationRepositoryImpl @Inject constructor(
    private val nowLocationRemoteDataSource: NowLocationRemoteDataSource,
) : NowLocationRepository {

    override suspend fun getBusesOnRoute(busRouteId: String): List<BusCurrentInformation> {
        return nowLocationRemoteDataSource.getBusNowLocation(busRouteId)
    }

    override suspend fun getSubwayTrains(subwayNumber: Int): List<TrainLocationInfoDomain> {
        return nowLocationRemoteDataSource.getSubwayTrainNowStation(subwayNumber)
            .map { it.toDomain() }
    }
}