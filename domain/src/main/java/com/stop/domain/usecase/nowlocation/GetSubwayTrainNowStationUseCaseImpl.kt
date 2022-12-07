package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.ApiChangedException
import com.stop.domain.model.AvailableTrainNoExistException
import com.stop.domain.model.nowlocation.TrainLocationInfoDomain
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.seoul.subway.TransportDirectionType
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class GetSubwayTrainNowStationUseCaseImpl @Inject constructor(
    private val nowLocationRepository: NowLocationRepository
) : GetSubwayTrainNowStationUseCase {

    override suspend operator fun invoke(
        transportLastTime: TransportLastTime,
        subwayNumber: Int
    ): TrainLocationInfoDomain {
        val trains = nowLocationRepository.getSubwayTrains(subwayNumber)

        val enableDestinations =
            transportLastTime.enableDestinationStation.map { it.stationName }
        val enableCurrentStations = transportLastTime.stationsUntilStart.map { it.stationName }

        val possibleTrains = trains.filter {
            enableDestinations.contains(it.destinationStationName)
                    && enableCurrentStations.contains(it.currentStationName)
                    && it.subwayDirection == transportLastTime.transportDirectionType
        }.sortedBy {
            enableCurrentStations.indexOf(it.currentStationName)
        }

        if (possibleTrains.isEmpty()) {
            throw AvailableTrainNoExistException()
        }

        return when (transportLastTime.transportDirectionType) {
            TransportDirectionType.OUTER, TransportDirectionType.TO_FIRST -> possibleTrains.first()
            TransportDirectionType.INNER, TransportDirectionType.TO_END -> possibleTrains.last()
            TransportDirectionType.UNKNOWN -> throw ApiChangedException()
        }
    }

}