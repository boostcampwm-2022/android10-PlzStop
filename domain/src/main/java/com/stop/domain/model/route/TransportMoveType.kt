package com.stop.domain.model.route

enum class TransportMoveType {
    BUS, SUBWAY;

    companion object {
        fun getMoveTypeByName(name: String): TransportMoveType? {
            return values().firstOrNull {
                it.name == name
            }
        }
    }
}