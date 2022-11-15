package com.stop.data.remote.model.route

import com.stop.domain.model.RouteResponse

data class RouteResponse(
    val metaData: MetaData
) {
    fun toDomain(): RouteResponse {
        TODO("수신한 데이터 클래스를 viewModel에서 사용할 데이터 클래스로 변환합니다.")
    }
}