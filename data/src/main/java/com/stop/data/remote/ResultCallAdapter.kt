package com.stop.data.remote

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import com.stop.data.remote.model.NetworkResult

internal class ResultCallAdapter<R : Any>(
    private val responseType: Type
) : CallAdapter<R, Call<NetworkResult<R>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Call<NetworkResult<R>> {
        return ResultCall(call)
    }

    class Factory : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {
            if (getRawType(returnType) != Call::class.java) {
                return null
            }

            check(returnType is ParameterizedType) {
                RETURN_TYPE_IS_NOT_PARAMETERIZED_TYPE
            }

            val responseType = getParameterUpperBound(0, returnType)

            if (getRawType(responseType) != NetworkResult::class.java) {
                return null
            }

            check(responseType is ParameterizedType) {
                RESPONSE_TYPE_IS_NOT_PARAMETERIZED_TYPE
            }

            val successBodyType = getParameterUpperBound(0, responseType)

            return ResultCallAdapter<Any>(successBodyType)
        }
    }

    companion object {
        private const val RETURN_TYPE_IS_NOT_PARAMETERIZED_TYPE =
            "return type must be parameterized as Call<Result<Foo>> or Call<Result<out Foo>>"

        private const val RESPONSE_TYPE_IS_NOT_PARAMETERIZED_TYPE =
            "Response must be parameterized as Result<Foo> or Result<out Foo>"
    }
}