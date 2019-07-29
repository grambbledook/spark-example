package com.github.grambbledook.example.spark.domain

import com.github.grambbledook.example.spark.domain.error.BusinessCode

sealed class Result

data class WorkflowSuccess<T>(val payload: T) : Result()
data class WorkflowFailure(val code: BusinessCode, val message: String) : Result()
data class ServiceFailure(val code: BusinessCode, val message: String) : Result()
