package com.stop.model

import com.stop.R

enum class ErrorType(val stringResourcesId: Int) {
    NO_START(R.string.no_start_input),
    NO_END(R.string.no_end_input),
    NO_ROUTE_RESULT(R.string.route_result_not_exist),
    TRANSPORT_LAST_TIME_IS_NOT_RECEIVED_YET(R.string.transport_last_time_is_not_received_yet),
    AVAILABLE_TRAIN_NO_EXIST_YET(R.string.available_train_no_exist_yet),
    API_CHANGED(R.string.api_changed),
    AVAILABLE_BUS_NO_EXIST_YET(R.string.available_bus_no_exist_yet),
    BUS_DISAPPEAR_SUDDENLY(R.string.bus_disappear_suddenly),
    MISSION_SOMETHING_WRONG(R.string.mission_something_wrong),
    SOCKET_TIMEOUT_EXCEPTION(R.string.socket_timeout_exception_please_retry),
    UNKNOWN_EXCEPTION(R.string.unknown_exception_occur),
    UNKNOWN_HOST_EXCEPTION(R.string.unknown_host_exception_occur),
}