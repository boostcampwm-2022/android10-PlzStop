package com.stop.data.repository

import com.stop.data.remote.source.nowlocation.NowLocationRemoteDataSource
import com.stop.domain.model.nowlocation.BusInfoUseCaseItem
import com.stop.domain.model.nowlocation.SubwayTrainRealTimePositionUseCaseItem
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class NowLocationRepositoryImpl @Inject constructor(
    private val nowLocationRemoteDataSource: NowLocationRemoteDataSource,
) : NowLocationRepository{

    override suspend fun getBusNowLocation(busRouteId: String, order: Int): BusInfoUseCaseItem {
        return nowLocationRemoteDataSource.getBusNowLocation(busRouteId, order).toUseCaseModel()
    }

    override suspend fun getSubwayTrainNowStation(trainNumber: String, subwayNumber: Int): SubwayTrainRealTimePositionUseCaseItem {
        return nowLocationRemoteDataSource.getSubwayTrainNowStation(trainNumber, subwayNumber).toUseCaseModel()
    }

}