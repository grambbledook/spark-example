package com.github.grambbledook.example.spark.fixture

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule


interface JacksonFixture {

    fun <T> T.toJsonString(): String {
        return Holder.mapper.writeValueAsString(this)
    }

    fun <T: Any> fromJsonString(json: String, clazz: Class<T>): T {
        return Holder.mapper.readValue(json, clazz)
    }

    fun <T: Any> fromJsonString(json: String, type: TypeReference<T>): T {
        return Holder.mapper.readValue(json, type)
    }

    object Holder {
        val mapper = ObjectMapper().apply {
            registerKotlinModule()
        }
    }
}

