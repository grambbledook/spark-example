package com.github.grambbledook.example.spark.handler

import arrow.core.Try
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.Jackson
import spark.Request

abstract class AbstractJsonHandler<T>(override val mapper: ObjectMapper, private val clazz: Class<T>) : AbstractHandler<T>(), Jackson {

    override fun getValue(request: Request): Try<T> {
        return Try.invoke {
            mapper.readValue(request.body(), clazz)
        }
    }

}