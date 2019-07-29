package com.github.grambbledook.example.spark.handler.traits

import com.fasterxml.jackson.databind.ObjectMapper

interface Jackson {
    val mapper: ObjectMapper

    fun <T> T.asJsonString(): String = mapper.writeValueAsString(this)
}