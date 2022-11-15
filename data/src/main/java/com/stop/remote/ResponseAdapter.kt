package com.stop.remote

import com.stop.remote.model.NetworkResult
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResponseAdapter<T>(
    private val successType: Type
) : CallAdapter<T, Call<NetworkResult<T>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<NetworkResult<T>> = ResponseCall(call)

}