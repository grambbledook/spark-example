package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.dto.WorkflowSuccess
import spark.ResponseTransformer

class JsonTransformer(private val mapper: ObjectMapper) : ResponseTransformer {
    override fun render(model: Any?): String {
        val payload = when(model) {
            is WorkflowSuccess<*> -> model.payload
            else -> model
        }
        return mapper.writeValueAsString(payload)
    }
}