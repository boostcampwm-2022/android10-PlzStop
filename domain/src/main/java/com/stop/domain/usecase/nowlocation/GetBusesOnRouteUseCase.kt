package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.route.TransportLastTime

interface GetBusesOnRouteUseCase {

    suspend operator fun invoke(transportLastTime: TransportLastTime): List<String>
}