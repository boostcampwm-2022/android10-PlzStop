package com.stop.domain.model

data class RouteRequest(
    val startX: String,
    val startY: String,
    val endX: String,
    val endY: String,
    val lang: Int = 0,
    val format: String = "json",
    val count: Int = 10,
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "startX" to startX,
            "startY" to startY,
            "endX" to endX,
            "endY" to endY,
            "lang" to lang.toString(),
            "format" to format,
            "count" to count.toString(),
        )
    }
}