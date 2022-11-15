package com.stop.remote.model

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data = data)

    class Error<T>(errorMessage: String) : NetworkResult<T>(message = errorMessage)

    class Loading<T> : NetworkResult<T>()
}