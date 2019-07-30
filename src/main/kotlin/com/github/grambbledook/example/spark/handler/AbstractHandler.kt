package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractHandler<I, O> : HandlerMixin<I, O> {
    override val logger: Logger = LoggerFactory.getLogger(javaClass::class.java)
}