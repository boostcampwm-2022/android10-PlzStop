package com.stop.domain.usecase.route

import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.tmap.custom.Itinerary

interface GetLastTransportTimeUseCase {

    suspend operator fun invoke(itinerary: Itinerary): List<TransportLastTime?>
}