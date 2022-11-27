package com.stop.domain.usecase.route

import com.stop.domain.model.route.TransportLastTimeInfo
import com.stop.domain.model.route.tmap.custom.Itinerary

interface GetLastTransportTimeUseCase {

    suspend fun getLastTransportTime(itinerary: Itinerary): TransportLastTimeInfo?
}