package com.stop.data.remote

import com.squareup.moshi.Moshi
import com.stop.data.remote.model.ErrorResponse
import com.stop.data.remote.model.NetworkResult
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ResponseCall<T> constructor(
    private val callDelegate: Call<T>
) : Call<NetworkResult<T>> {

    override fun enqueue(callback: Callback<NetworkResult<T>>) = callDelegate.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if(response.code() == 204){
                callback.onResponse(this@ResponseCall, Response.success(NetworkResult.Failure(code = response.code(), message = "No Content Error")))
            }

            response.body()?.let {
                if (response.isSuccessful) {
                    callback.onResponse(this@ResponseCall, Response.success(NetworkResult.Success(it)))
                } else {
                    val errorResponse: ErrorResponse? =
                        convertErrorBody(response.errorBody())
                    callback.onResponse(
                        this@ResponseCall,
                        Response.success(
                            NetworkResult.Failure(
                                code = response.code(),
                                message = errorResponse?.error?.message ?: "Something went wrong"
                            )
                        )
                    )
                }
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            when (t) {
                is HttpException -> {
                    callback.onResponse(this@ResponseCall, Response.success(NetworkResult.NetworkError(t as IOException)))
                }
                is IOException -> {
                    callback.onResponse(this@ResponseCall, Response.success(NetworkResult.Unexpected(t)))
                }
                else -> {
                    callback.onResponse(this@ResponseCall, Response.success(NetworkResult.Unexpected(t)))
                }
            }
        }
    })

    private fun convertErrorBody(errorBody: ResponseBody?): ErrorResponse? {
        return try {
            errorBody?.source()?.let {
                val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            null
        }
    }

    override fun clone(): Call<NetworkResult<T>> = ResponseCall(callDelegate.clone())

    override fun execute(): Response<NetworkResult<T>> = throw UnsupportedOperationException("ResponseCall does not support execute.")

    override fun isExecuted(): Boolean = callDelegate.isExecuted

    override fun cancel() = callDelegate.cancel()

    override fun isCanceled(): Boolean = callDelegate.isCanceled

    override fun request(): Request = callDelegate.request()

    override fun timeout(): Timeout = callDelegate.timeout()

}