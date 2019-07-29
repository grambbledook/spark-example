package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import spark.ResponseTransformer

class JsonTransformer(private val mapper: ObjectMapper) : ResponseTransformer {
    override fun render(model: Any?): String {
        return mapper.writeValueAsString(this)
    }
}