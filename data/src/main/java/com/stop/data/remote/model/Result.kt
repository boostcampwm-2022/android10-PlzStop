package com.stop.data.remote.model

import java.io.IOException

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val code: Int, val message: String?): Result<Nothing>()
    data class NetworkError(val exception: IOException): Result<Nothing>()
    data class Unexpected(val t: Throwable?): Result<Nothing>()
}
