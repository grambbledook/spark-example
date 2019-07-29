package com.github.grambbledook.example.spark.handler

import arrow.core.Try
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request

abstract class AbstractJsonHandler<T>(override val mapper: ObjectMapper, private val clazz: Class<T>) : AbstractHandler<T>(mapper) {
    override fun getValue(request: Request): Try<T> {
        return Try.invoke {
            mapper.readValue(request.body(), clazz)
        }
    }


}