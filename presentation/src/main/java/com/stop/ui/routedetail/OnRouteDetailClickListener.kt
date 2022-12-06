package com.stop.ui.routedetail

import com.stop.domain.model.route.tmap.custom.Route

interface OnRouteDetailClickListener {
    fun clickRouteDetail(route: Route)
}