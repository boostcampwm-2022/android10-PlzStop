package com.stop.data.remote.adapter.route

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.stop.data.remote.model.NetworkResult
import java.io.IOException

internal class ResultCall<T : Any>(private val call: Call<T>) : Call<NetworkResult<T>> {

    override fun clone(): Call<NetworkResult<T>> {
        return ResultCall(call.clone())
    }

    override fun execute(): Response<NetworkResult<T>> {
        throw UnsupportedOperationException(EXECUTE_IS_NOT_SUPPORTED)
    }

    override fun enqueue(callback: Callback<NetworkResult<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(NetworkResult.Unexpected(Throwable(SUCCESS_BUT_BODY_IS_NULL)))
                        )
                        return
                    }
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(NetworkResult.Success(responseBody))
                    )
                } else {
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(
                            NetworkResult.Failure(
                                response.code(),
                                response.errorBody()?.string()
                            )
                        )
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val networkResponse = when (t) {
                    is IOException -> NetworkResult.NetworkError(t)
                    else -> NetworkResult.Unexpected(t)
                }
                callback.onResponse(this@ResultCall, Response.success(networkResponse))
            }
        })
    }

    override fun isExecuted(): Boolean {
        return call.isExecuted
    }

    override fun cancel() {
        call.cancel()
    }

    override fun isCanceled(): Boolean {
        return call.isCanceled
    }

    override fun request(): Request {
        return call.request()
    }

    override fun timeout(): Timeout {
        return call.timeout()
    }

    companion object {
        private const val EXECUTE_IS_NOT_SUPPORTED = "ResultCall doesn't support execute"
        private const val SUCCESS_BUT_BODY_IS_NULL = "요청에 성공했지만 body가 비어있습니다."
    }
}