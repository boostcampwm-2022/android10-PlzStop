package com.stop.data.remote

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlResponse

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonResponse

class JsonXmlConverterFactory(
    private val factories: Map<Class<*>, Converter.Factory>
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return annotations.mapNotNull {
            factories[it.annotationClass.javaObjectType]
        }.getOrNull(0)?.responseBodyConverter(type, annotations, retrofit)
    }

    class Builder {

        private val factories = hashMapOf<Class<*>, Converter.Factory>()

        fun setXmlConverterFactory(converterFactory: Converter.Factory): Builder {
            factories[XmlResponse::class.java] = converterFactory
            return this
        }

        fun setJsonConverterFactory(converterFactory: Converter.Factory): Builder {
            factories[JsonResponse::class.java] = converterFactory
            return this
        }

        @Suppress("unused")
        fun addCustomConverterFactory(
            annotation: Class<out Annotation>,
            converterFactory: Converter.Factory
        ): Builder {
            factories[annotation] = converterFactory
            return this
        }

        fun build(): JsonXmlConverterFactory {
            return JsonXmlConverterFactory(factories)
        }
    }
}