package com.github.grambbledook.example.spark.handler

import arrow.core.Try
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.Jackson
import spark.Request
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

@Suppress("UNCHECKED_CAST")
abstract class AbstractJsonHandler<I, O>(override val mapper: ObjectMapper) : AbstractHandler<I, O>(), Jackson {
    private val requestType = constructRequestType()

    private fun constructRequestType(): Class<I> {
        return (javaClass.genericSuperclass as ParameterizedTypeImpl).actualTypeArguments[0] as Class<I>
    }

    override fun getValue(request: Request): Try<I> {
        return Try.invoke {
            mapper.readValue<I>(request.body(), requestType)
        }
    }

}