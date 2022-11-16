package com.stop.model.route

data class RouteRequest(
    val originName: String,
    val originX: String,
    val originY: String,
    val destinationName: String,
    val destinationX: String,
    val destinationY: String,
): java.io.Serializable {

    fun getOrigin(): Place {
        return Place(
            name = originName,
            coordinate = Coordinate(
                latitude = originX,
                longitude = originY,
            )
        )
    }

    fun getDestination(): Place {
        return Place(
            name = destinationName,
            coordinate = Coordinate(
                latitude = destinationX,
                longitude = destinationX,
            )
        )
    }
}
