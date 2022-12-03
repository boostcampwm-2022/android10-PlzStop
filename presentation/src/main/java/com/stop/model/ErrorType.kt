package com.stop.model

import com.stop.R

enum class ErrorType(val stringResourcesId: Int) {
    NO_START(R.string.no_start_input),
    NO_END(R.string.no_end_input),
    NO_ROUTE_RESULT(R.string.route_result_not_exist)
}