package com.stop.ui.routedetail

import com.stop.domain.model.route.tmap.custom.Coordinate

interface OnRouteItemClickListener {
    fun clickRouteItem(coordinate: Coordinate)
}