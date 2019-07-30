package com.github.grambbledook.example.spark.dto

import com.github.grambbledook.example.spark.dto.error.ErrorCode

sealed class Result

data class WorkflowSuccess<T>(val payload: T) : Result()
data class WorkflowFailure(val code: ErrorCode, val message: String) : Result()
data class ServiceFailure(val code: ErrorCode, val message: String) : Result()
