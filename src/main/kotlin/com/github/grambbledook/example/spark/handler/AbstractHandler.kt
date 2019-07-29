package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractHandler<T>(override val mapper: ObjectMapper) : HandlerMixin<T> {
    override val logger: Logger = LoggerFactory.getLogger(javaClass::class.java)
}