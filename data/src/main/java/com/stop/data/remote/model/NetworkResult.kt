package com.stop.data.remote.model

import java.io.IOException

internal sealed class NetworkResult<out R> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Failure(val code: Int, val message: String?): NetworkResult<Nothing>()
    data class NetworkError(val exception: IOException): NetworkResult<Nothing>()
    data class Unexpected(val exception: Throwable): NetworkResult<Nothing>()
}
