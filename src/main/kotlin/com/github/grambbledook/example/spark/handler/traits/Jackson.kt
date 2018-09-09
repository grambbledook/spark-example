package com.github.grambbledook.example.spark.handler.traits

import com.fasterxml.jackson.databind.ObjectMapper

interface Jackson<T> {
    val mapper: ObjectMapper

    fun toString(payload: Any): String = mapper.writeValueAsString(payload)
}